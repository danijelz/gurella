package com.gurella.studio.launch;

import static com.gurella.studio.launch.SceneLauncherConstants.ATTR_SCENE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.utils.Try;

public class SelectSceneTab extends AbstractLaunchConfigurationTab {
	private Text projectText;
	private Button searchProjectButton;

	private Text sceneText;
	private Button searchSceneButton;

	@Override
	public void createControl(Composite parent) {
		Composite comp = createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_BOTH);
		((GridLayout) comp.getLayout()).verticalSpacing = 0;
		createProjectEditor(comp);
		createVerticalSpacer(comp, 1);
		setControl(comp);
		createSceneEditor(comp);
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
		projectText = createSingleText(group, 1);
		projectText.addModifyListener(e -> updateLaunchConfigurationDialog());
		ControlAccessibleListener.addListener(projectText, group.getText());
		searchProjectButton = createPushButton(group, "&Browse...", null);
		searchProjectButton.addListener(SWT.Selection, e -> selectProject());
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

	protected void createSceneEditor(Composite parent) {
		Group group = createGroup(parent, "&Scene", 2, 1, GridData.FILL_HORIZONTAL);
		sceneText = createSingleText(group, 1);
		sceneText.addModifyListener(e -> updateLaunchConfigurationDialog());
		ControlAccessibleListener.addListener(sceneText, group.getText());
		searchSceneButton = createPushButton(group, "&Search...", null);
		searchSceneButton.addListener(SWT.Selection, e -> selectScene());
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(ATTR_MAIN_TYPE_NAME, LaunchSceneApplication.class.getName());
		config.setAttribute(ATTR_DEFAULT_CLASSPATH, false);
		config.setAttribute(ATTR_PROJECT_NAME, findContextProjectName());
		config.setAttribute(ATTR_SCENE_NAME, findContextSceneName());
	}

	private static IWorkbenchPage getActivePage() {
		IWorkbenchWindow w = GurellaStudioPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		return w == null ? null : w.getActivePage();
	}

	private static String findContextProjectName() {
		IWorkbenchPage page = getActivePage();
		if (page == null) {
			return "";
		}

		ISelection selection = page.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object obj = ss.getFirstElement();

			if (obj instanceof IJavaElement) {
				return ((IJavaElement) obj).getJavaProject().getElementName();
			}

			if (obj instanceof IResource) {
				IResource resource = (IResource) obj;
				IJavaElement javaElement = JavaCore.create(resource);
				if (javaElement == null) {
					IProject project = resource.getProject();
					javaElement = JavaCore.create(project);
				}

				if (javaElement != null && javaElement.exists()) {
					return javaElement.getJavaProject().getElementName();
				}
			}
		}

		IEditorPart part = page.getActiveEditor();
		if (part != null) {
			IEditorInput input = part.getEditorInput();
			IJavaElement javaElement = input.getAdapter(IJavaElement.class);
			if (javaElement != null) {
				return javaElement.getJavaProject().getElementName();
			}

			IResource resource = input.getAdapter(IResource.class);
			if (resource != null) {
				IJavaProject javaProject = JavaCore.create(resource.getProject());
				if (javaProject != null && javaProject.exists()) {
					return javaProject.getElementName();
				}
			}
		}

		return "";
	}

	private static String findContextSceneName() {
		IWorkbenchPage page = getActivePage();
		if (page == null) {
			return "";
		}

		ISelection selection = page.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object obj = ss.getFirstElement();
			if (obj instanceof IFile) {
				IFile sceneFile = (IFile) obj;
				if ("gscn".equals(sceneFile.getLocation().getFileExtension())) {
					return getAssetsRelativePath(sceneFile);
				}
			}
		}

		IEditorPart part = page.getActiveEditor();
		if (!(part instanceof SceneEditor)) {
			return "";
		}

		IFileEditorInput input = (IFileEditorInput) part.getEditorInput();
		IFile sceneFile = input.getFile();
		return getAssetsRelativePath(sceneFile);
	}

	private static String getAssetsRelativePath(IResource resource) {
		IProject project = resource.getProject();
		IFolder assetsFolder = project.getFolder("assets");
		return resource.getLocation().makeRelativeTo(assetsFolder.getLocation()).toString();
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		updateProjectFromConfig(configuration);
		updateSceneFromConfig(configuration);
	}

	private void updateProjectFromConfig(ILaunchConfiguration config) {
		Try.ofFailable(() -> config.getAttribute(ATTR_PROJECT_NAME, "")).onFailure(e -> setErrorMessage(e))
				.onSuccess(n -> projectText.setText(n));
	}

	private void setErrorMessage(Throwable throwable) {
		if (throwable instanceof CoreException) {
			setErrorMessage(((CoreException) throwable).getStatus().getMessage());
		} else {
			setErrorMessage(throwable.getMessage());
		}
	}

	protected void updateSceneFromConfig(ILaunchConfiguration config) {
		Try.ofFailable(() -> config.getAttribute(ATTR_SCENE_NAME, "")).onFailure(e -> setErrorMessage(e))
				.onSuccess(n -> sceneText.setText(n));
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(ATTR_PROJECT_NAME, projectText.getText().trim());
		config.setAttribute(ATTR_SCENE_NAME, sceneText.getText().trim());
		config.setAttribute(ATTR_MAIN_TYPE_NAME, LaunchSceneApplication.class.getName());
		config.setAttribute(ATTR_CLASSPATH, computeClasspath());
		config.setAttribute(ATTR_DEFAULT_CLASSPATH, false);
		mapResources(config);
	}

	private List<String> computeClasspath() {
		IJavaProject javaProject = getJavaProject();
		return javaProject == null || !javaProject.exists() ? null : SceneLauncher.computeClasspath(javaProject);
	}

	private void mapResources(ILaunchConfigurationWorkingCopy config) {
		IJavaProject javaProject = getJavaProject();
		if (javaProject == null || !javaProject.exists() || !javaProject.isOpen()) {
			return;
		}

		Try.ofFailable(() -> getResource(config)).onFailure(e -> setErrorMessage(e))
				.onSuccess(r -> config.setMappedResources(r == null ? null : new IResource[] { r }));
	}

	private static IResource getResource(ILaunchConfiguration candidate) throws CoreException {
		String projectName = candidate.getAttribute(ATTR_PROJECT_NAME, "");
		if (!Path.ROOT.isValidSegment(projectName)) {
			return null;
		}

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		String typeName = candidate.getAttribute(ATTR_MAIN_TYPE_NAME, "");
		if (Values.isBlank(typeName)) {
			return project;

		}

		if (project == null || !project.isAccessible()) {
			return project;
		}

		IJavaProject javaProject = JavaCore.create(project);
		if (javaProject == null || !javaProject.exists()) {
			return project;
		}

		typeName = typeName.replace('$', '.');
		IType type = javaProject.findType(typeName);
		if (type == null) {
			return project;
		}

		IResource resource = type.getUnderlyingResource();
		if (resource == null) {
			resource = type.getAdapter(IResource.class);
		}

		return resource == null ? project : resource;
	}

	private void selectScene() {
		IJavaProject javaProject = getJavaProject();
		IContainer container = javaProject == null || !javaProject.exists() ? getWorkspaceRoot()
				: javaProject.getProject();
		FilteredResourcesSelectionDialog dialog = new FilteredResourcesSelectionDialog(getShell(), false, container,
				IResource.FILE);
		dialog.setInitialPattern("*.gscn", FilteredItemsSelectionDialog.CARET_BEGINNING);

		if (dialog.open() == Window.OK) {
			IFile sceneFile = (IFile) dialog.getResult()[0];
			IProject project = sceneFile.getProject();
			projectText.setText(project.getName());

			IFolder assetsFolder = project.getFolder("assets");
			IPath relative = sceneFile.getLocation().makeRelativeTo(assetsFolder.getLocation());
			sceneText.setText(relative.toString());
		}
	}

	@Override
	public String getName() {
		return "Main";
	}

	@Override
	public Image getImage() {
		return GurellaStudioPlugin.getImage("icons/Umbrella-16.png");
	}

	private void selectProject() {
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
			IJavaProject project = (IJavaProject) dialog.getFirstResult();
			projectText.setText(project.getElementName());
		}
	}

	private static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	private IJavaProject getJavaProject() {
		String projectName = projectText.getText().trim();
		return projectName.length() < 1 ? null : JavaCore.create(getWorkspaceRoot()).getJavaProject(projectName);
	}

	@Override
	public boolean isValid(ILaunchConfiguration config) {
		setErrorMessage((String) null);
		setMessage(null);

		String name = Try.ofFailable(() -> config.getAttribute(ATTR_PROJECT_NAME, "")).orElse("");
		if (name.length() == 0) {
			setErrorMessage("Project not specified");
			return false;
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateName(name, IResource.PROJECT);
		if (!status.isOK()) {
			setErrorMessage(NLS.bind("Illegal project name: {0}", new String[] { status.getMessage() }));
			return false;
		}

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (!project.exists()) {
			setErrorMessage(NLS.bind("Project {0} does not exist", new String[] { name }));
			return false;
		}

		if (!project.isOpen()) {
			setErrorMessage(NLS.bind("Project {0} is closed", new String[] { name }));
			return false;
		}

		name = Try.ofFailable(() -> config.getAttribute(ATTR_SCENE_NAME, "")).orElse("");
		if (name.length() == 0) {
			setErrorMessage("Scene not specified");
			return false;
		}

		IFile sceneFile = project.getFolder("assets").getFile(name);
		if (!sceneFile.exists()) {
			setErrorMessage(NLS.bind("Scene {0} does not exist", new String[] { name }));
			return false;
		}

		if (!"gscn".equals(sceneFile.getLocation().getFileExtension())) {
			setErrorMessage(NLS.bind("Illegal scene name: {0}", new String[] { status.getMessage() }));
			return false;
		}

		return true;
	}
}
