package com.gurella.studio.editor;

import static com.gurella.studio.GurellaStudioPlugin.showError;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
import org.eclipse.swt.widgets.Control;
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
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.SequenceGenerator;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.assets.AssetsView;
import com.gurella.studio.editor.common.ErrorComposite;
import com.gurella.studio.editor.control.Dock;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.event.DispatcherEvent;
import com.gurella.studio.editor.graph.SceneGraphView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.subscription.SceneChangedListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;

public class SceneEditor extends EditorPart implements SceneLoadedListener, SceneChangedListener {
	public final int id = SequenceGenerator.next();

	private Composite contentComposite;
	private Dock dock;

	IUndoContext undoContext;
	IOperationHistory operationHistory;
	UndoActionHandler undoAction;
	RedoActionHandler redoAction;
	private UndoRedoActionGroup historyActionGroup;

	List<DockableView> registeredViews = new ArrayList<DockableView>();
	private SceneEditorContext context;

	private SwtLwjglApplication application;
	private SceneEditorApplicationListener applicationListener;

	private boolean dirty;


	@Override
	public void doSave(IProgressMonitor monitor) {
		Try.ofFailable(monitor, m -> save(m)).onFailure(e -> showError(e, "Error saving scene"));
		monitor.done();
	}

	private void save(IProgressMonitor monitor) throws CoreException {
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		IPath path = input.getFile().getFullPath();
		JsonOutput output = new JsonOutput();
		String string = output.serialize(Scene.class, context.getScene());
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
	public boolean isSaveAsAllowed() {
		return false;
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

		historyActionGroup = new UndoRedoActionGroup(site, undoContext, true);
		historyActionGroup.fillActionBars(site.getActionBars());

		applicationListener = new SceneEditorApplicationListener(id);
		context = new SceneEditorContext(this);

		EventService.subscribe(id, this);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void createPartControl(Composite parent) {
		this.contentComposite = parent;
		parent.setLayout(new GridLayout());

		dock = new Dock(this, parent, SWT.NONE);
		dock.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		synchronized (GurellaStudioPlugin.glMutex) {
			application = new SwtLwjglApplication(dock.getCenter(), applicationListener);
		}

		SceneEditorRegistry.put(this, dock, application, context);

		SceneGraphView sceneGraphView = new SceneGraphView(this, SWT.LEFT);
		registeredViews.add(sceneGraphView);
		registeredViews.add(new AssetsView(this, SWT.LEFT));
		registeredViews.add(new InspectorView(this, SWT.RIGHT));
		dock.setSelection(sceneGraphView);

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

	public Dock getDock() {
		return dock;
	}

	public SceneEditorContext getContext() {
		return context;
	}

	@Override
	public void setFocus() {
		dock.setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		EventService.unsubscribe(id, this);
		// TODO context and applicationListener should be unified
		context.dispose();
		applicationListener.debugUpdate();
		application.exit();
		SceneEditorRegistry.remove(this);
		
		historyActionGroup.dispose();
		operationHistory.dispose(undoContext, true, true, true);
		redoAction.dispose();
		undoAction.dispose();
	}

	@Override
	public void sceneChanged() {
		dirty = true;
		firePropertyChange(PROP_DIRTY);
	}

	public static int getEditorId(Control control) {
		return SceneEditorRegistry.getEditorId(control);
	}

	public static int getCurrentEditorId() {
		return SceneEditorRegistry.getCurrentEditorId();
	}

	public static SceneEditor getCurrentEditor() {
		return SceneEditorRegistry.getCurrentEditor();
	}

	public static void subscribe(Object subscriber) {
		EventService.subscribe(getCurrentEditorId(), subscriber);
	}

	public static void subscribe(int editorId, Object subscriber) {
		EventService.subscribe(editorId, subscriber);
	}

	public static void subscribe(Control subscriber) {
		EventService.subscribe(getEditorId(subscriber), subscriber);
	}

	public static void subscribe(int editorId, Control subscriber) {
		EventService.subscribe(editorId, subscriber);
	}

	public static void unsubscribe(Object subscriber) {
		EventService.unsubscribe(getCurrentEditorId(), subscriber);
	}

	public static void unsubscribe(int editorId, Object subscriber) {
		EventService.unsubscribe(editorId, subscriber);
	}

	public static void unsubscribe(Control subscriber) {
		EventService.unsubscribe(getEditorId(subscriber), subscriber);
	}

	public static void unsubscribe(int editorId, Control subscriber) {
		EventService.unsubscribe(editorId, subscriber);
	}

	public static <L extends EventSubscription> void post(Control source, Event<L> event) {
		EventService.post(getEditorId(source), event);
	}

	public static <L extends EventSubscription> void post(int editorId, Event<L> event) {
		EventService.post(editorId, event);
	}

	public static <L extends EventSubscription> void post(Control source, Class<L> type, Consumer<L> dispatcher) {
		EventService.post(getEditorId(source), new DispatcherEvent<L>(type, dispatcher));
	}

	public static <L extends EventSubscription> void post(int editorId, Class<L> type, Consumer<L> dispatcher) {
		EventService.post(editorId, new DispatcherEvent<L>(type, dispatcher));
	}

	private final class LoadSceneCallback extends AsyncCallbackAdapter<Scene> {
		private Label progressLabel;

		public LoadSceneCallback() {
			GLCanvas glCanvas = application.getGraphics().getGlCanvas();
			glCanvas.setLayout(new GridLayout());
			progressLabel = new Label(glCanvas, SWT.DM_FILL_NONE);
			progressLabel.setBackground(glCanvas.getDisplay().getSystemColor(SWT.COLOR_TRANSPARENT));
			progressLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
			progressLabel.setText("Loading...");
		}

		@Override
		public void onProgress(float progress) {
			asyncExec(() -> updateProgress((int) (progress * 100)));
		}

		private void updateProgress(int progress) {
			progressLabel.setText("Loading... " + progress);
		}

		@Override
		public void onSuccess(Scene scene) {
			DispatcherEvent.post(id, SceneLoadedListener.class, l -> l.sceneLoaded(scene));
			scene.start();
			asyncExec(() -> progressLabel.dispose());
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
