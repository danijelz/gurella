package com.gurella.studio.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.gurella.studio.wizard.setup.Dependency;
import com.gurella.studio.wizard.setup.DependencyBank;
import com.gurella.studio.wizard.setup.DependencyBank.ProjectDependency;
import com.gurella.studio.wizard.setup.DependencyBank.ProjectType;
import com.gurella.studio.wizard.setup.Executor.LogCallback;
import com.gurella.studio.wizard.setup.GdxSetup;
import com.gurella.studio.wizard.setup.ProjectBuilder;

public class NewProjectWizard extends Wizard implements INewWizard {
	private NewProjectWizardPage page;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		page = new NewProjectWizardPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		try {
			performFinishSafely();
			return true;
		} catch (CoreException e) {
			ExceptionHandler.handle(e, getShell(), "New", "Creation of project failed.");
		} catch (InvocationTargetException e) {
			ExceptionHandler.handle(e, getShell(), "New", "Creation of project failed.");
		} catch (InterruptedException e) {
		} catch (Exception e) {
			ExceptionHandler.handle(new InvocationTargetException(e), getShell(), "New", "Creation of project failed.");
		}

		return false;
	}

	private void performFinishSafely() throws Exception {
		DependencyBank bank = new DependencyBank();
		List<ProjectType> modules = new ArrayList<ProjectType>();
		modules.add(ProjectType.CORE);
		modules.add(ProjectType.DESKTOP);
		// modules.add(ProjectType.ANDROID);
		// modules.add(ProjectType.IOSMOE);
		// modules.add(ProjectType.HTML);

		List<Dependency> dependencies = new ArrayList<Dependency>();
		dependencies.add(bank.getDependency(ProjectDependency.GDX));
		dependencies.add(bank.getDependency(ProjectDependency.BULLET));
		dependencies.add(bank.getDependency(ProjectDependency.BOX2D));

		ProjectBuilder builder = new ProjectBuilder(bank, modules, dependencies);
		buildProjects(builder);

		String projectLocation = page.getProjectLocation();
		openProject(projectLocation, "");
		openProject(projectLocation, "core");
		openProject(projectLocation, "desktop");
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
		final String name = page.getProjectName();
		final String destination = page.getProjectLocation();

		final String pack = "com.packagename";
		final String clazz = "TestApp";
		final String sdkLocation = "sdk";
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

		Job job = Job.getJobManager().currentJob();
		ISchedulingRule rule = job == null ? null : job.getRule();
		IWorkspaceRunnable op = new BuildProjectsRunnable(builder, destination, pack, name, clazz, sdkLocation);
		IRunnableWithProgress runnable = rule != null ? new WorkbenchRunnableAdapter(op, rule, true)
				: new WorkbenchRunnableAdapter(op, ResourcesPlugin.getWorkspace().getRoot());
		getContainer().run(true, true, runnable);
	}

	private static final class BuildProjectsRunnable implements IWorkspaceRunnable, LogCallback {
		private final ProjectBuilder builder;
		private final String destination;
		private final String pack;
		private final String name;
		private final String clazz;
		private final String sdkLocation;

		private BuildProjectsRunnable(ProjectBuilder builder, String destination, String pack, String name,
				String clazz, String sdkLocation) {
			this.builder = builder;
			this.destination = destination;
			this.pack = pack;
			this.name = name;
			this.clazz = clazz;
			this.sdkLocation = sdkLocation;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
			long millis = System.currentTimeMillis();
			log("Generating app in " + destination + "\n");
			new GdxSetup().build(builder, destination, name, pack, clazz, sdkLocation, this);
			log("Done! " + (String.valueOf(System.currentTimeMillis() - millis)) + "\n");
		}

		@Override
		public void log(String log) {
			// TODO log to text
			System.out.print(log);
		}
	}
}
