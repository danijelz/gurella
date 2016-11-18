package com.gurella.studio.editor;

import static com.gurella.studio.GurellaStudioPlugin.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IPathEditorInput;

import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.persister.AssetPersister;
import com.gurella.engine.asset.persister.AssetPersisters;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.history.HistoryManager;
import com.gurella.studio.editor.subscription.EditorCloseListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;
import com.gurella.studio.editor.utils.Try;

public class SceneEditorContext implements SceneLoadedListener, EditorCloseListener {
	public final int editorId;
	private final HistoryManager historyManager;

	public final IPathEditorInput editorInput;
	public final IResource sceneResource;
	public final IWorkspace workspace;
	public final IProject project;
	public final IJavaProject javaProject;
	// TODO make private and expose methods: loadClass(), createType()...
	public final ClassLoader classLoader;

	private Scene scene;
	private Map<String, Object> editedAssets = new HashMap<>();

	public SceneEditorContext(SceneEditor editor) {
		editorId = editor.id;
		historyManager = editor.historyManager;

		editorInput = (IPathEditorInput) editor.getEditorInput();
		sceneResource = editorInput.getAdapter(IResource.class);
		workspace = sceneResource.getWorkspace();
		project = sceneResource.getProject();
		javaProject = JavaCore.create(project);
		classLoader = DynamicURLClassLoader.newInstance(javaProject);
		Reflection.classResolver = classLoader::loadClass;
		EventService.subscribe(editorId, this);
	}

	@Override
	public void onEditorClose() {
		EventService.unsubscribe(editorId, this);
		unloadAssets();
		Optional.ofNullable(scene).ifPresent(s -> s.stop());
		String msg = "Error closing java project";
		Try.successful(Optional.ofNullable(javaProject)).filter(o -> o.isPresent()).map(o -> o.get())
				.peek(t -> t.close()).onFailure(e -> log(e, msg));
	}

	private void unloadAssets() {
		for (Entry<String, Object> entry : editedAssets.entrySet()) {
			Object asset = entry.getValue();
			AssetService.unload(asset);
		}
	}

	void persist(IProgressMonitor monitor) {
		for (Entry<String, Object> entry : editedAssets.entrySet()) {
			String fileName = entry.getKey();
			Object asset = entry.getValue();
			AssetPersister<Object> persister = AssetPersisters.get(asset);
			if (persister == null) {
				// TODO exception?
			} else {
				persister.persist(fileName, asset);
			}
		}
	}

	public Scene getScene() {
		return scene;
	}

	@Override
	public void sceneLoaded(Scene scene) {
		this.scene = scene;
	}

	public void executeOperation(IUndoableOperation operation, String errorMsg) {
		historyManager.executeOperation(operation, errorMsg);
	}

	public <T> T load(String fileName) {
		Object asset = editedAssets.get(fileName);
		if (asset == null) {
			asset = AssetService.load(fileName);
			editedAssets.put(fileName, asset);
		}
		return Values.cast(asset);
	}

	public void unload(String fileName) {
		Object asset = editedAssets.remove(fileName);
		if (asset != null) {
			AssetService.unload(fileName);
		}
	}
}
