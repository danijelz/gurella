package com.gurella.studio.launch;

//import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH;
//import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
//import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
//import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.gurella.studio.GurellaStudioPlugin;

public class SelectSceneTab extends AbstractLaunchConfigurationTab {
	private Text fMainText;
	private Button fSearchButton;
	private Text fProjText;
	private Button fProjButton;
	private WidgetListener fListener = new WidgetListener();

	@Override
	public void createControl(Composite parent) {
		Composite comp = createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_BOTH);
		((GridLayout) comp.getLayout()).verticalSpacing = 0;
		createProjectEditor(comp);
		createVerticalSpacer(comp, 1);
		setControl(comp);
		// TODO Auto-generated method stub
	}

	private static Composite createComposite(Composite parent, Font font, int columns, int hspan, int fill) {
		Composite g = new Composite(parent, SWT.NONE);
		g.setLayout(new GridLayout(columns, false));
		g.setFont(font);
		GridData gd = new GridData(fill);
		gd.horizontalSpan = hspan;
		g.setLayoutData(gd);
		return g;
	}

	private void createProjectEditor(Composite parent) {
		Group group = createGroup(parent, "&Project:", 2, 1, GridData.FILL_HORIZONTAL);
		fProjText = createSingleText(group, 1);
		fProjText.addModifyListener(fListener);
		ControlAccessibleListener.addListener(fProjText, group.getText());
		fProjButton = createPushButton(group, "&Browse...", null);
		fProjButton.addSelectionListener(fListener);
	}

	private static Group createGroup(Composite parent, String text, int columns, int hspan, int fill) {
		Group g = new Group(parent, SWT.NONE);
		g.setLayout(new GridLayout(columns, false));
		g.setText(text);
		g.setFont(parent.getFont());
		GridData gd = new GridData(fill);
		gd.horizontalSpan = hspan;
		g.setLayoutData(gd);
		return g;
	}

	private static Text createSingleText(Composite parent, int hspan) {
		Text t = new Text(parent, SWT.SINGLE | SWT.BORDER);
		t.setFont(parent.getFont());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		t.setLayoutData(gd);
		return t;
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IJavaElement javaElement = getContext();
		if (javaElement != null) {
			initializeJavaProject(javaElement, configuration);
		} else {
			configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
		}
		// initializeMainTypeAndName(javaElement, configuration);

		// configuration.setAttribute(ATTR_PROJECT_NAME, projectName);
		// configuration.setAttribute(ATTR_MAIN_TYPE_NAME, main);
		// configuration.setAttribute(ATTR_VM_ARGUMENTS, vmArguments);
		// configuration.setAttribute(ATTR_CLASSPATH, getClasspath(javaProject));
		configuration.setAttribute(ATTR_DEFAULT_CLASSPATH, false);

		// TODO Auto-generated method stub
	}

	private static IJavaElement getContext() {
		IWorkbenchPage page = getActivePage();
		if (page != null) {
			ISelection selection = page.getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection) selection;
				if (!ss.isEmpty()) {
					Object obj = ss.getFirstElement();
					if (obj instanceof IJavaElement) {
						return (IJavaElement) obj;
					}

					if (obj instanceof IResource) {
						IJavaElement je = JavaCore.create((IResource) obj);
						if (je == null) {
							IProject pro = ((IResource) obj).getProject();
							je = JavaCore.create(pro);
						}

						if (je != null) {
							return je;
						}
					}
				}
			}

			IEditorPart part = page.getActiveEditor();
			if (part != null) {
				IEditorInput input = part.getEditorInput();
				IJavaElement javaElement = input.getAdapter(IJavaElement.class);
				if (javaElement != null) {
					return javaElement;
				}

				IResource resource = input.getAdapter(IResource.class);
				if (resource != null) {
					return JavaCore.create(resource.getProject());
				}
			}
		}

		return null;
	}

	private static IWorkbenchPage getActivePage() {
		IWorkbenchWindow w = getActiveWorkbenchWindow();
		if (w != null) {
			return w.getActivePage();
		}
		return null;
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return GurellaStudioPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	private static void initializeJavaProject(IJavaElement javaElement, ILaunchConfigurationWorkingCopy config) {
		IJavaProject javaProject = javaElement.getJavaProject();
		String name = null;
		if (javaProject != null && javaProject.exists()) {
			name = javaProject.getElementName();
		}
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, name);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		updateProjectFromConfig(configuration);
		// setCurrentLaunchConfiguration(configuration);
		// updateMainTypeFromConfig(configuration);
		// updateStopInMainFromConfig(configuration);
		// updateInheritedMainsFromConfig(configuration);
		// updateExternalJars(configuration);
	}

	private void updateProjectFromConfig(ILaunchConfiguration config) {
		String projectName = "";
		try {
			projectName = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
		} catch (CoreException ce) {
			setErrorMessage(ce.getStatus().getMessage());
		}
		fProjText.setText(projectName);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getName() {
		return "Main";
	}

	@Override
	public Image getImage() {
		return GurellaStudioPlugin.getImage("icons/Umbrella-16.png");
	}

	private void handleProjectButtonSelected() {
		IJavaProject project = chooseJavaProject();
		if (project == null) {
			return;
		}
		String projectName = project.getElementName();
		fProjText.setText(projectName);
	}

	private IJavaProject chooseJavaProject() {
		ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle("Project Selection");
		dialog.setMessage("Select a project to constrain your search.");

		try {
			dialog.setElements(JavaCore.create(getWorkspaceRoot()).getJavaProjects());
		} catch (JavaModelException jme) {
			GurellaStudioPlugin.log(jme, "Internal Error");
		}

		IJavaProject javaProject = getJavaProject();
		if (javaProject != null) {
			dialog.setInitialSelections(new Object[] { javaProject });
		}

		if (dialog.open() == Window.OK) {
			return (IJavaProject) dialog.getFirstResult();
		}

		return null;
	}

	private static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	private IJavaProject getJavaProject() {
		String projectName = fProjText.getText().trim();
		if (projectName.length() < 1) {
			return null;
		}
		return getJavaModel().getJavaProject(projectName);
	}

	private static IJavaModel getJavaModel() {
		return JavaCore.create(getWorkspaceRoot());
	}

	private class WidgetListener implements ModifyListener, SelectionListener {
		@Override
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			/* do nothing */}

		@Override
		public void widgetSelected(SelectionEvent e) {
			Object source = e.getSource();
			if (source == fProjButton) {
				handleProjectButtonSelected();
			} else {
				updateLaunchConfigurationDialog();
			}
		}
	}
}
