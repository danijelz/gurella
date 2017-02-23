package com.gurella.studio.wizard.project;

import static com.gurella.studio.editor.utils.Try.unchecked;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

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
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.wizard.project.build.ProjectBuilder;
import com.gurella.studio.wizard.project.build.Executor.LogCallback;

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
			buldProjects();
			GurellaStudioPlugin.log(IStatus.INFO, detailsPage.getLog());
			return true;
		} catch (Exception e) {
			GurellaStudioPlugin.log(IStatus.ERROR, detailsPage.getLog());
			GurellaStudioPlugin.showError(e, "Creation of project failed.");
			return false;
		}
	}

	private void buldProjects() throws Exception {
		ProjectSetup projectSetup = new ProjectSetup();
		mainPage.updateProjectSetup(projectSetup);
		detailsPage.updateProjectSetup(projectSetup);

		getContainer().run(true, true, new BuildProjectsRunnable(projectSetup));

		IWorkingSet[] workingSets = mainPage.getWorkingSets();
		String location = projectSetup.location;
		openProject(location, "", workingSets);
		projectSetup.projects.stream().forEach(p -> unchecked(() -> openProject(location, p.getName(), workingSets)));
	}

	private static void openProject(String path, String name, IWorkingSet[] workingSets) throws CoreException {
		Path descriptionFile = new Path(path + File.separator + name + File.separator + ".project");
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.loadProjectDescription(descriptionFile);
		IProject project = workspace.getRoot().getProject(description.getName());
		project.create(description, null);
		project.open(null);

		if (workingSets.length > 0) {
			PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(project, workingSets);
		}
	}

	private final class BuildProjectsRunnable implements IRunnableWithProgress, IThreadListener, LogCallback {
		private final ProjectSetup projectSetup;

		private final ISchedulingRule rule;
		private final boolean transferRule;

		private BuildProjectsRunnable(ProjectSetup projectSetup) {
			this.projectSetup = projectSetup;

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
			log("Generating app in " + projectSetup.location + "\n");
			ProjectBuilder.build(projectSetup, this);
			log("Done!\n");
		}

		@Override
		public void log(String text) {
			detailsPage.log(text);
		}

		@Override
		public void threadChange(Thread thread) {
			if (transferRule) {
				Job.getJobManager().transferRule(rule, thread);
			}
		}
	}
}
