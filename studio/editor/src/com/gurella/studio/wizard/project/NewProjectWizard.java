package com.gurella.studio.wizard.project;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.IThreadListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.wizard.project.setup.Dependency;
import com.gurella.studio.wizard.project.setup.ProjectType;
import com.gurella.studio.wizard.project.setup.ScriptBuilder;
import com.gurella.studio.wizard.project.setup.Setup;
import com.gurella.studio.wizard.project.setup.SetupInfo;
import com.gurella.studio.wizard.project.setup.Executor.LogCallback;

public class NewProjectWizard extends Wizard implements INewWizard {
	private NewProjectMainPage mainPage;
	private NewProjectDetailsPage detailsPage;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		mainPage = new NewProjectMainPage();
		addPage(mainPage);

		detailsPage = new NewProjectDetailsPage();
		addPage(detailsPage);
	}

	@Override
	public boolean performFinish() {
		try {
			performFinishSafely();
			return true;
		} catch (Exception e) {
			GurellaStudioPlugin.showError(e, "Creation of project failed.");
			return false;
		}
	}

	private void performFinishSafely() throws Exception {
		SetupInfo setupInfo = new SetupInfo();
		mainPage.updateSetupInfo(setupInfo);
		detailsPage.updateSetupInfo(setupInfo);
		
		List<ProjectType> modules = new ArrayList<ProjectType>();
		modules.add(ProjectType.CORE);
		modules.add(ProjectType.DESKTOP);
		modules.add(ProjectType.IOSMOE);
		modules.add(ProjectType.ANDROID);
		// modules.add(ProjectType.HTML);

		List<Dependency> dependencies = new ArrayList<Dependency>();
		dependencies.add(Dependency.GDX);
		dependencies.add(Dependency.BULLET);
		dependencies.add(Dependency.BOX2D);
		dependencies.add(Dependency.GURELLA);

		ScriptBuilder scriptBuilder = new ScriptBuilder(modules, dependencies);
		IRunnableWithProgress runnable = new BuildProjectsRunnable(scriptBuilder);
		getContainer().run(true, true, runnable);

		String projectLocation = mainPage.getProjectLocation();
		openProject(projectLocation, "");
		openProject(projectLocation, "core");
		openProject(projectLocation, "desktop");
		openProject(projectLocation, "ios-moe");
		openProject(projectLocation, "android");
		// openProject(projectLocation, "html");
	}

	private static void openProject(String path, String name) throws CoreException {
		Path descriptionFile = new Path(path + File.separator + name + File.separator + ".project");
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.loadProjectDescription(descriptionFile);
		IProject project = workspace.getRoot().getProject(description.getName());
		project.create(description, null);
		project.open(null);
	}

	private final class BuildProjectsRunnable implements IRunnableWithProgress, IThreadListener, LogCallback {
		private final ScriptBuilder scriptBuilder;

		private final String name;
		private final String location;
		private final String pack;
		private final String clazz;
		private final String androidSdkLocation;
		private final String androidAPILevel;
		private final String androidBuildToolsVersion;

		private final ISchedulingRule rule;
		private final boolean transferRule;

		private final StringBuilder log = new StringBuilder();

		private BuildProjectsRunnable(ScriptBuilder scriptBuilder) {
			this.scriptBuilder = scriptBuilder;
			this.name = mainPage.getProjectName();
			this.location = mainPage.getProjectLocation();
			this.pack = detailsPage.getPackageName();
			this.clazz = detailsPage.getMainClassName();
			this.androidSdkLocation = detailsPage.getAndroidSdkLocation();
			this.androidAPILevel = detailsPage.getAndroidApiLevel();
			this.androidBuildToolsVersion = detailsPage.getAndroidBuildToolsVersion();

			Job job = Job.getJobManager().currentJob();
			if (job == null) {
				rule = ResourcesPlugin.getWorkspace().getRoot();
				transferRule = false;
			} else {
				rule = job.getRule();
				transferRule = true;
			}
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			try {
				JavaCore.run(this::runBuilder, rule, monitor);
			} catch (OperationCanceledException e) {
				throw new InterruptedException(e.getMessage());
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			}
		}

		private void runBuilder(IProgressMonitor monitor) throws OperationCanceledException {
			log("Generating app in " + location + "\n");
			Setup.build(scriptBuilder, location, name, pack, clazz, androidSdkLocation, androidAPILevel,
					androidBuildToolsVersion, this);
			log("Done!\n");
		}

		@Override
		public void log(String text) {
			log.append(text);
			detailsPage.setLog(log.toString());
			GurellaStudioPlugin.log(IStatus.INFO, text);
		}

		@Override
		public void threadChange(Thread thread) {
			if (transferRule) {
				Job.getJobManager().transferRule(rule, thread);
			}
		}
	}
}
