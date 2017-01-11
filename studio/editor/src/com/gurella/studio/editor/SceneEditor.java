package com.gurella.studio.editor;

import static com.gurella.studio.GurellaStudioPlugin.showError;

import java.util.function.Consumer;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.async.AsyncCallbackAdapter;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.serialization.json.JsonOutput;
import com.gurella.engine.utils.Sequence;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.control.Dock;
import com.gurella.studio.editor.control.ViewRegistry;
import com.gurella.studio.editor.dnd.DndAssetPlacementManager;
import com.gurella.studio.editor.history.HistoryManager;
import com.gurella.studio.editor.launch.LaunchManager;
import com.gurella.studio.editor.preferences.PreferencesManager;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.SceneDirtyListener;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;
import com.gurella.studio.editor.swtgl.SwtLwjglGraphics;
import com.gurella.studio.editor.ui.ErrorComposite;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;

public class SceneEditor extends EditorPart implements SceneDirtyListener, EditorCloseListener {
	public final int id = Sequence.next();

	private Composite content;
	private Dock dock;

	SceneEditorContext sceneContext;
	private SceneProvider sceneProvider;
	ViewRegistry viewRegistry;
	private DndAssetPlacementManager dndAssetPlacementManager;
	HistoryManager historyManager;
	private LaunchManager launchManager;
	private PreferencesManager preferencesManager;

	private SwtLwjglApplication application;

	private boolean dirty;

	@Override
	public void doSave(IProgressMonitor monitor) {
		String msg = "Error saving scene";
		Try.successful(monitor).peek(this::save).onSuccess(m -> m.done()).onFailure(e -> showError(e, msg));
	}

	private void save(IProgressMonitor monitor) throws CoreException {
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		IFile file = input.getFile();
		IPath path = file.getFullPath();

		monitor.beginTask("Saving scene", 2000);
		ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
		manager.connect(path, LocationKind.IFILE, monitor);

		JsonOutput output = new JsonOutput();
		String relativeFileName = file.getProjectRelativePath().toPortableString();
		String serialized = output.serialize(new FileHandle(relativeFileName), Scene.class, sceneContext.getScene());
		String pretty = new JsonReader().parse(serialized).prettyPrint(OutputType.minimal, 120);

		ITextFileBuffer buffer = ITextFileBufferManager.DEFAULT.getTextFileBuffer(path, LocationKind.IFILE);
		buffer.getDocument().set(pretty);
		buffer.commit(monitor, true);

		sceneContext.persist(monitor);
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
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void createPartControl(Composite parent) {
		this.content = parent;
		parent.setLayout(new GridLayout());

		dock = new Dock(parent, id);
		dock.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		application = new SwtLwjglApplication(getInternalAssetsPath(), dock.getCenter(), id);
		SceneEditorRegistry.put(this, parent, application);

		synchronized (SwtLwjglGraphics.glMutex) {
			application.init();
			sceneProvider = new SceneProvider(id);
			preferencesManager = new PreferencesManager(this);
			historyManager = new HistoryManager(this);
			sceneContext = new SceneEditorContext(this);
			launchManager = new LaunchManager(sceneContext);
			viewRegistry = new ViewRegistry(sceneContext, dock);
			// TODO create canvas in editor and pass it to consumers
			GLCanvas glCanvas = application.getGraphics().getGlCanvas();
			dndAssetPlacementManager = new DndAssetPlacementManager(id, glCanvas);
			EventService.subscribe(id, this);
		}

		String path = ((IPathEditorInput) getEditorInput()).getPath().toString();
		AssetService.loadAsync(path, Scene.class, new LoadSceneCallback(), 0);
	}

	private String getInternalAssetsPath() {
		IResource resource = getEditorInput().getAdapter(IResource.class);
		return resource.getProject().getFile("assets").getLocation().toString();
	}

	private void presentException(Throwable exception) {
		UiUtils.disposeChildren(content);
		String message = "Error opening scene";
		IStatus status = GurellaStudioPlugin.log(exception, message);
		ErrorComposite errorComposite = new ErrorComposite(content, status, message);
		errorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		content.layout();
	}

	@Override
	public void setFocus() {
		dock.setFocus();
	}

	@Override
	public void onEditorClose() {
		EventService.unsubscribe(id, this);
		SceneEditorRegistry.remove(this);
	}

	@Override
	public void sceneDirty() {
		dirty = true;
		firePropertyChange(PROP_DIRTY);
	}

	public SceneEditorContext getSceneContext() {
		return sceneContext;
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

	public static void subscribeToCurrentEditor(EventSubscription subscriber) {
		EventService.subscribe(getCurrentEditorId(), subscriber);
	}

	public static <T extends Control & EventSubscription> void subscribeToControlEditor(T subscriber) {
		EventService.subscribe(getEditorId(subscriber), subscriber);
	}

	public static void unsubscribeFromCurrentEditor(EventSubscription subscriber) {
		EventService.unsubscribe(getCurrentEditorId(), subscriber);
	}

	public static <T extends Control & EventSubscription> void unsubscribeFromControlEditor(T subscriber) {
		EventService.unsubscribe(getEditorId(subscriber), subscriber);
	}

	public static <L extends EventSubscription> void postToControlEditor(Control source, Event<L> event) {
		EventService.post(getEditorId(source), event);
	}

	public static <L extends EventSubscription> void postToControlEditor(Control source, Class<L> type,
			Consumer<L> dispatcher) {
		EventService.post(getEditorId(source), type, l -> dispatcher.accept(l));
	}

	public static <L extends EventSubscription> void postToCurrentEditor(Event<L> event) {
		EventService.post(getCurrentEditorId(), event);
	}

	public static <L extends EventSubscription> void postToCurrentEditor(Class<L> type, Consumer<L> dispatcher) {
		EventService.post(getCurrentEditorId(), type, l -> dispatcher.accept(l));
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
			sceneProvider.setScene(scene);
			asyncExec(() -> progressLabel.dispose());
		}

		@Override
		public void onException(Throwable exception) {
			asyncExec(() -> presentException(exception));
		}

		private void asyncExec(Runnable runnable) {
			content.getDisplay().asyncExec(runnable);
		}
	}
}
