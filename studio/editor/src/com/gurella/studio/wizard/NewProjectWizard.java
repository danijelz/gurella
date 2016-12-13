package com.gurella.studio.wizard;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.wizard.setup.Dependency;
import com.gurella.studio.wizard.setup.DependencyBank;
import com.gurella.studio.wizard.setup.DependencyBank.ProjectDependency;
import com.gurella.studio.wizard.setup.DependencyBank.ProjectType;
import com.gurella.studio.wizard.setup.Executor.CharCallback;
import com.gurella.studio.wizard.setup.GdxSetup;
import com.gurella.studio.wizard.setup.ProjectBuilder;


public class NewProjectWizard extends Wizard implements INewWizard {
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private NewProjectWizardPage page; 

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}
	
	@Override
	public void addPages() {
		page = new NewProjectWizardPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		DependencyBank bank = new DependencyBank();
		ProjectBuilder builder = new ProjectBuilder(bank);
		List<ProjectType> modules = new ArrayList<ProjectType>();
		modules.add(ProjectType.CORE);
		modules.add(ProjectType.DESKTOP);
//		modules.add(ProjectType.ANDROID);
//		modules.add(ProjectType.IOSMOE);
//		modules.add(ProjectType.HTML);

		List<Dependency> dependencies = new ArrayList<Dependency>();
		dependencies.add(bank.getDependency(ProjectDependency.GDX));
		dependencies.add(bank.getDependency(ProjectDependency.BULLET));
		dependencies.add(bank.getDependency(ProjectDependency.BOX2D));
		
		builder.buildProject(modules, dependencies);
		try {
			builder.build();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		final String name = "projectname";
		final String pack = "com.packagename";
		final String clazz = "TestApp";
		//final String destination = "/media/danijel/data/testproj/";
		final String destination = "D:\\gurella\\testproj";
		final String sdkLocation = "sdk";
//		if (!GdxSetup.isSdkLocationValid(sdkLocation) && modules.contains(ProjectType.ANDROID)) {
//			JOptionPane
//					.showMessageDialog(this,
//							"Your Android SDK path doesn't contain an SDK! Please install the Android SDK, including all platforms and build tools!");
//			return;
//		}
//
//		if (modules.contains(ProjectType.ANDROID)) {
//			if (!GdxSetup.isSdkUpToDate(sdkLocation)) {
//				File sdkLocationFile = new File(sdkLocation);
//				try {  //give them a poke in the right direction
//					if (System.getProperty("os.name").contains("Windows")) {
//						String replaced = sdkLocation.replace("\\", "\\\\");
//						Runtime.getRuntime().exec("\"" + replaced + "\\SDK Manager.exe\"");
//					} else {
//						File sdkManager = new File(sdkLocation, "tools/android");
//						Runtime.getRuntime().exec(new String[] {sdkManager.getAbsolutePath(), "sdk"});
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				return;
//			}
//		}
//
//		if (!GdxSetup.isEmptyDirectory(destination)) {
//			int value = JOptionPane.showConfirmDialog(this, "The destination is not empty, do you want to overwrite?", "Warning!", JOptionPane.YES_NO_OPTION);
//			if (value != 0) {
//				return;
//			}
//		}
//
//		List<String> incompatList = builder.buildProject(modules, dependencies);
//		if (incompatList.size() == 0) {
//			try {
//				builder.build();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} else {
//			JPanel panel = new JPanel();
//			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//			for (String subIncompat : incompatList) {
//				JLabel label = new JLabel(subIncompat);
//				label.setAlignmentX(Component.CENTER_ALIGNMENT);
//				panel.add(label);
//			}
//
//			JLabel infoLabel = new JLabel("<html><br><br>The project can be generated, but you wont be able to use these extensions in the respective sub modules<br>Please see the link to learn about extensions</html>");
//			infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//			panel.add(infoLabel);
//			JEditorPane pane = new JEditorPane("text/html", "<a href=\"https://github.com/libgdx/libgdx/wiki/Dependency-management-with-Gradle\">Dependency Management</a>");
//			pane.addHyperlinkListener(new HyperlinkListener() {
//				@Override
//				public void hyperlinkUpdate(HyperlinkEvent e) {
//					if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
//						try {
//							Desktop.getDesktop().browse(new URI(e.getURL().toString()));
//						} catch (IOException e1) {
//							e1.printStackTrace();
//						} catch (URISyntaxException e1) {
//							e1.printStackTrace();
//						}
//				}
//			});
//			pane.setEditable(false);
//			pane.setOpaque(false);
//			pane.setAlignmentX(Component.CENTER_ALIGNMENT);
//			panel.add(pane);
//
//			Object[] options = {"Yes, build it!", "No, I'll change my extensions"};
//			int value = JOptionPane.showOptionDialog(null, panel, "Extension Incompatibilities", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
//			if (value != 0) {
//				return;
//			} else {
//				try {
//					builder.build();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		
		boolean offline = true;
		final List<String> gradleArgs = new ArrayList<String>();
	    gradleArgs.add("--no-daemon");
	    gradleArgs.add("eclipse");
	    gradleArgs.add("afterEclipseImport");
	    if (offline) {
			gradleArgs.add("--offline");
		}
	    
		IWorkspaceRunnable op = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
				long millis = System.currentTimeMillis();
				System.out.println("Generating app in " + destination);
				new GdxSetup().build(builder, destination, name, pack, clazz, sdkLocation, new CharCallback() {
					@Override
					public void character(char c) {
						System.out.print(c);
					}
				}, gradleArgs);
				System.out.println("Done! " + String.valueOf(System.currentTimeMillis() - millis));
			}
		};
		
		try {
			ISchedulingRule rule= null;
			Job job= Job.getJobManager().currentJob();
			if (job != null) {
				rule= job.getRule();
			}
			IRunnableWithProgress runnable= null;
			if (rule != null) {
				runnable= new WorkbenchRunnableAdapter(op, rule, true);
			} else {
				runnable= new WorkbenchRunnableAdapter(op, ResourcesPlugin.getWorkspace().getRoot());
			}
			getContainer().run(true, true, runnable);
		} catch (InvocationTargetException e) {
			handleFinishException(getShell(), e);
			return false;
		} catch  (InterruptedException e) {
			return false;
		}
		return true;

//		new Thread() {
//			public void run () {
//				log("Generating app in " + destination);
//				new GdxSetup().build(builder, destination, name, pack, clazz, sdkLocation, new CharCallback() {
//					@Override
//					public void character (char c) {
//						log(c);
//					}
//				}, gradleArgs);
//				log("Done!");
//				if (ui.settings.getGradleArgs().contains("eclipse")) {
//					log("To import in Eclipse: File -> Import -> General -> Existing Projects into Workspace");
//				}
//			}
//		}.start();
	}
	
	private void handleFinishException(Shell shell, InvocationTargetException e) {
		String title= NewWizardMessages.NewElementWizard_op_error_title;
		String message= NewWizardMessages.NewElementWizard_op_error_message;
		ExceptionHandler.handle(e, shell, title, message);
	}
}
