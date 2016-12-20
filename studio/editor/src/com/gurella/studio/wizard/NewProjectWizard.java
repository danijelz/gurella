package com.gurella.studio.wizard;

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
import com.gurella.studio.wizard.setup.Dependency;
import com.gurella.studio.wizard.setup.DependencyBank;
import com.gurella.studio.wizard.setup.DependencyBank.ProjectDependency;
import com.gurella.studio.wizard.setup.DependencyBank.ProjectType;
import com.gurella.studio.wizard.setup.Executor.LogCallback;
import com.gurella.studio.wizard.setup.GdxSetup;
import com.gurella.studio.wizard.setup.ProjectBuilder;

public class NewProjectWizard extends Wizard implements INewWizard {
	private static final String GRADLE_USER_HOME_CLASSPATH_VARIABLE_NAME = "GRADLE_USER_HOME";
	private static final String MOE_USER_HOME_CLASSPATH_VARIABLE_NAME = "MOE_USER_HOME";

	private NewProjectWizardPageOne pageOne;
	private NewProjectWizardPageTwo pageTwo;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		pageOne = new NewProjectWizardPageOne();
		addPage(pageOne);

		pageTwo = new NewProjectWizardPageTwo();
		addPage(pageTwo);
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
		DependencyBank bank = new DependencyBank();
		List<ProjectType> modules = new ArrayList<ProjectType>();
		modules.add(ProjectType.CORE);
		modules.add(ProjectType.DESKTOP);
		//modules.add(ProjectType.ANDROID);
		//modules.add(ProjectType.HTML);
		modules.add(ProjectType.IOSMOE);

		List<Dependency> dependencies = new ArrayList<Dependency>();
		dependencies.add(bank.getDependency(ProjectDependency.GDX));
		dependencies.add(bank.getDependency(ProjectDependency.BULLET));
		dependencies.add(bank.getDependency(ProjectDependency.BOX2D));

		ProjectBuilder builder = new ProjectBuilder(bank, modules, dependencies);
		buildProjects(builder);

		String projectLocation = pageOne.getProjectLocation();
		openProject(projectLocation, "");
		openProject(projectLocation, "core");
		openProject(projectLocation, "desktop");
		//openProject(projectLocation, "android");
		//openProject(projectLocation, "html");
		openProject(projectLocation, "ios-moe");
	}

	private static void openProject(String path, String name) throws CoreException {
		Path descriptionFile = new Path(path + File.separator + name + File.separator + ".project");
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.loadProjectDescription(descriptionFile);
		IProject project = workspace.getRoot().getProject(description.getName());
		project.create(description, null);
		project.open(null);
	}

	private void buildProjects(ProjectBuilder builder) throws InvocationTargetException, InterruptedException {
		// if (!GdxSetup.isSdkLocationValid(sdkLocation) && modules.contains(ProjectType.ANDROID)) {
		// JOptionPane
		// .showMessageDialog(this,
		// "Your Android SDK path doesn't contain an SDK! Please install the Android SDK, including all platforms and
		// build tools!");
		// return;
		// }
		//
		// if (modules.contains(ProjectType.ANDROID)) {
		// if (!GdxSetup.isSdkUpToDate(sdkLocation)) {
		// File sdkLocationFile = new File(sdkLocation);
		// try { //give them a poke in the right direction
		// if (System.getProperty("os.name").contains("Windows")) {
		// String replaced = sdkLocation.replace("\\", "\\\\");
		// Runtime.getRuntime().exec("\"" + replaced + "\\SDK Manager.exe\"");
		// } else {
		// File sdkManager = new File(sdkLocation, "tools/android");
		// Runtime.getRuntime().exec(new String[] {sdkManager.getAbsolutePath(), "sdk"});
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// return;
		// }
		// }
		//
		// if (!GdxSetup.isEmptyDirectory(destination)) {
		// int value = JOptionPane.showConfirmDialog(this, "The destination is not empty, do you want to overwrite?",
		// "Warning!", JOptionPane.YES_NO_OPTION);
		// if (value != 0) {
		// return;
		// }
		// }
		//
		// List<String> incompatList = builder.buildProject(modules, dependencies);
		// if (incompatList.size() == 0) {
		// try {
		// builder.build();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// } else {
		// JPanel panel = new JPanel();
		// panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// for (String subIncompat : incompatList) {
		// JLabel label = new JLabel(subIncompat);
		// label.setAlignmentX(Component.CENTER_ALIGNMENT);
		// panel.add(label);
		// }
		//
		// JLabel infoLabel = new JLabel("<html><br><br>The project can be generated, but you wont be able to use these
		// extensions in the respective sub modules<br>Please see the link to learn about extensions</html>");
		// infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		// panel.add(infoLabel);
		// JEditorPane pane = new JEditorPane("text/html", "<a
		// href=\"https://github.com/libgdx/libgdx/wiki/Dependency-management-with-Gradle\">Dependency Management</a>");
		// pane.addHyperlinkListener(new HyperlinkListener() {
		// @Override
		// public void hyperlinkUpdate(HyperlinkEvent e) {
		// if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
		// try {
		// Desktop.getDesktop().browse(new URI(e.getURL().toString()));
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// } catch (URISyntaxException e1) {
		// e1.printStackTrace();
		// }
		// }
		// });
		// pane.setEditable(false);
		// pane.setOpaque(false);
		// pane.setAlignmentX(Component.CENTER_ALIGNMENT);
		// panel.add(pane);
		//
		// Object[] options = {"Yes, build it!", "No, I'll change my extensions"};
		// int value = JOptionPane.showOptionDialog(null, panel, "Extension Incompatibilities",
		// JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
		// if (value != 0) {
		// return;
		// } else {
		// try {
		// builder.build();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// }

		IRunnableWithProgress runnable = new BuildProjectsRunnable(builder);
		getContainer().run(true, true, runnable);
	}

	private final class BuildProjectsRunnable implements IRunnableWithProgress, IThreadListener, LogCallback {
		private final ProjectBuilder builder;

		private final String name;
		private final String location;
		private final String pack;
		private final String clazz;
		private final String sdkLocation;

		private final ISchedulingRule rule;
		private boolean transferRule;

		private BuildProjectsRunnable(ProjectBuilder builder) {
			this.builder = builder;
			this.name = pageOne.getProjectName();
			this.location = pageOne.getProjectLocation();
			this.pack = pageTwo.getPackageName();
			this.clazz = pageTwo.getClassName();
			this.sdkLocation = "/media/danijel/data/ddd/android/android-sdk-24.4.1/";

			Job job = Job.getJobManager().currentJob();
			if (job == null) {
				rule = ResourcesPlugin.getWorkspace().getRoot();
			} else {
				rule = job.getRule();
				transferRule = true;
			}
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			try {
				if (JavaCore.getClasspathVariable(GRADLE_USER_HOME_CLASSPATH_VARIABLE_NAME) == null) {
					Path path = new Path(System.getProperty("user.home") + File.separator + ".gradle");
					JavaCore.setClasspathVariable(GRADLE_USER_HOME_CLASSPATH_VARIABLE_NAME, path, monitor);
				}
				
				if (JavaCore.getClasspathVariable(MOE_USER_HOME_CLASSPATH_VARIABLE_NAME) == null) {
					Path path = new Path(System.getProperty("user.home") + File.separator + ".moe");
					JavaCore.setClasspathVariable(MOE_USER_HOME_CLASSPATH_VARIABLE_NAME, path, monitor);
				}
				
				JavaCore.run(this::runBuilder, rule, monitor);
			} catch (OperationCanceledException e) {
				throw new InterruptedException(e.getMessage());
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			}
		}

		private void runBuilder(IProgressMonitor monitor) throws OperationCanceledException {
			long millis = System.currentTimeMillis();
			log("Generating app in " + location + "\n");
			new GdxSetup().build(builder, location, name, pack, clazz, sdkLocation, this);
			log("Done! " + (String.valueOf(System.currentTimeMillis() - millis)) + "\n");
		}

		@Override
		public void log(String text) {
			pageTwo.log(text);
		}

		@Override
		public void threadChange(Thread thread) {
			if (transferRule) {
				Job.getJobManager().transferRule(rule, thread);
			}
		}
	}
}
