package com.gurella.studio.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.EditorPart;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.async.AsyncCallbackAdapter;
import com.gurella.engine.base.serialization.json.JsonOutput;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.SequenceGenerator;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.assets.AssetsExplorerView;
import com.gurella.studio.editor.common.ErrorComposite;
import com.gurella.studio.editor.event.SceneLoadedEvent;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.part.Dock;
import com.gurella.studio.editor.part.DockableView;
import com.gurella.studio.editor.scene.SceneHierarchyView;
import com.gurella.studio.editor.subscription.SceneChangedListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;
import com.gurella.studio.editor.utils.UiUtils;

public class SceneEditor extends EditorPart implements SceneLoadedListener, SceneChangedListener {
	public final int id = SequenceGenerator.next();

	private Composite contentComposite;
	private Dock partControl;

	IUndoContext undoContext;
	IOperationHistory operationHistory;
	UndoActionHandler undoAction;
	RedoActionHandler redoAction;

	List<DockableView> registeredViews = new ArrayList<DockableView>();
	private SceneEditorContext editorContext;

	private SwtLwjglApplication application;
	private SceneEditorApplicationListener applicationListener;

	private boolean dirty;

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			save(monitor);
		} catch (CoreException e) {
			String message = "Error saving scene";
			GurellaStudioPlugin.showError(e, message);
		} finally {
			monitor.done();
		}
	}

	private void save(IProgressMonitor monitor) throws CoreException {
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		IPath path = input.getFile().getFullPath();
		JsonOutput output = new JsonOutput();
		String string = output.serialize(Scene.class, editorContext.scene);
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

		undoContext = new UndoContext();
		undoAction = new UndoActionHandler(site, undoContext);
		redoAction = new RedoActionHandler(site, undoContext);

		IWorkbench workbench = getSite().getWorkbenchWindow().getWorkbench();
		operationHistory = workbench.getOperationSupport().getOperationHistory();

		UndoRedoActionGroup historyActionGroup = new UndoRedoActionGroup(site, undoContext, true);
		historyActionGroup.fillActionBars(site.getActionBars());

		editorContext = new SceneEditorContext(this);
		applicationListener = new SceneEditorApplicationListener(this);

		EventService.subscribe(id, this);
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

		partControl = new Dock(this, parent, SWT.NONE);
		partControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		synchronized (GurellaStudioPlugin.glMutex) {
			application = new SwtLwjglApplication(partControl.getCenter(), applicationListener);
		}

		SceneEditorUtils.put(this, partControl, application, editorContext);

		SceneHierarchyView sceneHierarchyView = new SceneHierarchyView(this, SWT.LEFT);
		registeredViews.add(sceneHierarchyView);
		AssetsExplorerView assetsExplorerView = new AssetsExplorerView(this, SWT.LEFT);
		registeredViews.add(assetsExplorerView);
		InspectorView inspectorView = new InspectorView(this, SWT.RIGHT);
		registeredViews.add(inspectorView);

		partControl.setSelection(sceneHierarchyView);

		IPathEditorInput pathEditorInput = (IPathEditorInput) getEditorInput();
		AssetService.loadAsync(pathEditorInput.getPath().toString(), Scene.class, new LoadSceneCallback(), 0);
	}

	@Override
	public void sceneLoaded(Scene scene) {
		dirty = false;
	}

	private void presentException(Throwable exception) {
		UiUtils.disposeChildren(contentComposite);
		String message = "Error opening scene";
		IStatus status = GurellaStudioPlugin.log(exception, message);
		ErrorComposite errorComposite = new ErrorComposite(contentComposite, status, message);
		errorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contentComposite.layout();
	}

	public Dock getPartControl() {
		return partControl;
	}

	public SceneEditorContext getEditorContext() {
		return editorContext;
	}

	@Override
	public void setFocus() {
		application.setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		EventService.unsubscribe(id, this);
		// TODO context and applicationListener should be unified
		editorContext.dispose();
		applicationListener.debugUpdate();
		application.exit();
		SceneEditorUtils.remove(this);
	}

	@Override
	public void sceneChanged() {
		dirty = true;
		firePropertyChange(PROP_DIRTY);
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
			EventService.post(id, new SceneLoadedEvent(scene));
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
