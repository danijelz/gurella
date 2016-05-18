package com.gurella.studio.editor;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.async.AsyncCallbackAdapter;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.base.serialization.json.JsonOutput;
import com.gurella.engine.scene.Scene;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.assets.AssetsExplorerView;
import com.gurella.studio.editor.common.ErrorComposite;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.scene.SceneEditorMainContainer;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.scene.SceneHierarchyView;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class GurellaSceneEditor extends EditorPart implements EditorMessageListener {
	private Composite contentComposite;
	private SceneEditorMainContainer mainContainer;

	List<SceneEditorView> registeredViews = new ArrayList<SceneEditorView>();
	private SceneEditorContext context;

	private SwtLwjglApplication application;
	private SceneEditorApplicationListener applicationListener;

	private boolean dirty;

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			save(monitor);
		} catch (CoreException e) {
			String message = "Error saving scene";
			IStatus status = GurellaStudioPlugin.log(e, message);
			ErrorDialog.openError(contentComposite.getShell(), message, e.getLocalizedMessage(), status);
		} finally {
			monitor.done();
		}
	}

	private void save(IProgressMonitor monitor) throws CoreException {
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		IPath path = input.getFile().getFullPath();
		JsonOutput output = new JsonOutput();
		String string = output.serialize(Scene.class, context.scene);
		monitor.beginTask("Saving", 2000);
		ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
		manager.connect(path, LocationKind.IFILE, monitor);
		ITextFileBuffer buffer = ITextFileBufferManager.DEFAULT.getTextFileBuffer(path, LocationKind.IFILE);
		buffer.getDocument().set(new JsonReader().parse(string).prettyPrint(OutputType.minimal, 120));
		buffer.commit(monitor, true);
		manager.disconnect(path, LocationKind.IFILE, monitor);
		dirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);

		IPathEditorInput pathEditorInput = (IPathEditorInput) input;
		String[] segments = pathEditorInput.getPath().segments();
		setPartName(segments[segments.length - 1]);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		this.contentComposite = parent;
		parent.setLayout(new GridLayout());

		context = new SceneEditorContext((IPathEditorInput) getEditorInput());
		context.addEditorMessageListener(this);

		mainContainer = new SceneEditorMainContainer(this, parent, SWT.NONE);
		mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		SceneHierarchyView sceneHierarchyView = new SceneHierarchyView(this, SWT.LEFT);
		registeredViews.add(sceneHierarchyView);
		AssetsExplorerView assetsExplorerView = new AssetsExplorerView(this, SWT.LEFT);
		registeredViews.add(assetsExplorerView);
		InspectorView inspectorView = new InspectorView(this, SWT.RIGHT);
		registeredViews.add(inspectorView);

		mainContainer.setSelection(sceneHierarchyView);
		Composite center = mainContainer.getCenter();

		applicationListener = new SceneEditorApplicationListener();
		context.addEditorMessageListener(applicationListener);
		
		synchronized (GurellaStudioPlugin.glMutex) {
			application = new SwtLwjglApplication(center, applicationListener);
		}

		IPathEditorInput pathEditorInput = (IPathEditorInput) getEditorInput();
		ResourceService.loadAsync(pathEditorInput.getPath().toString(), Scene.class, new LoadSceneCallback(), 0);
	}

	private void presentScene(Scene scene) {
		dirty = false;
		context.setScene(scene);
		applicationListener.presentScene(scene);
		
		GLCanvas glCanvas = application.getGraphics().getGlCanvas();
		Menu menu = new Menu(glCanvas);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Test");
		glCanvas.setMenu(menu);
	}

	private void presentException(Throwable exception) {
		Arrays.stream(contentComposite.getChildren()).forEach(c -> c.dispose());
		String message = "Error opening scene";
		IStatus status = GurellaStudioPlugin.log(exception, message);
		ErrorComposite errorComposite = new ErrorComposite(contentComposite, status, message);
		errorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contentComposite.layout();
	}

	public SceneEditorMainContainer getMainContainer() {
		return mainContainer;
	}

	public SceneEditorContext getContext() {
		return context;
	}

	public Scene getScene() {
		return context.scene;
	}

	public IWorkspace getWorkspace() {
		return context.workspace;
	}

	public IProject getProject() {
		return context.project;
	}

	public IJavaProject getJavaProject() {
		return context.javaProject;
	}

	public URLClassLoader getClassLoader() {
		return context.classLoader;
	}

	public void addEditorMessageListener(EditorMessageListener listener) {
		context.addEditorMessageListener(listener);
	}

	public void removeEditorMessageListener(EditorMessageListener listener) {
		context.removeEditorMessageListener(listener);
	}

	@Override
	public void setFocus() {
		application.setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		context.dispose();
		application.exit();
	}

	public void postMessage(Object source, Object message) {
		context.postMessage(source, message);
	}

	@Override
	public void handleMessage(Object source, Object message) {
		if (message instanceof SceneChangedMessage) {
			dirty = true;
			firePropertyChange(PROP_DIRTY);
		}
	}

	private final class LoadSceneCallback extends AsyncCallbackAdapter<Scene> {
		private Label label;

		public LoadSceneCallback() {
			GLCanvas glCanvas = application.getGraphics().getGlCanvas();
			glCanvas.setLayout(new GridLayout());
			label = new Label(glCanvas, SWT.DM_FILL_NONE);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
			label.setText("Loading...");
		}

		@Override
		public void onProgress(float progress) {
			asyncExec(() -> updateProgress((int) (progress * 100)));
		}

		private void updateProgress(int progress) {
			label.setText("Loading... " + progress);
		}

		@Override
		public void onSuccess(Scene scene) {
			presentScene(scene);
			asyncExec(() -> label.dispose());
		}

		@Override
		public void onException(Throwable exception) {
			asyncExec(() -> presentException(exception));
		}

		private void asyncExec(Runnable runnable) {
			contentComposite.getDisplay().asyncExec(runnable);
		}
	}
}
