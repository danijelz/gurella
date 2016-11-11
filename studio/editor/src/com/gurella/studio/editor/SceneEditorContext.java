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
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IPathEditorInput;
import org.osgi.service.prefs.Preferences;

import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.persister.AssetPersister;
import com.gurella.engine.asset.persister.AssetPersisters;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.graph.SceneGraphView;
import com.gurella.studio.editor.history.HistoryManager;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;
import com.gurella.studio.editor.utils.Try;

public class SceneEditorContext implements SceneLoadedListener, EditorPreCloseListener {
	public final int editorId;
	private final HistoryManager historyManager;

	public final IPathEditorInput editorInput;
	public final IWorkspace workspace;
	public final IProject project;
	public final IJavaProject javaProject;
	// TODO make private and expose methods: loadClass(), createType()...
	public final ClassLoader classLoader;

	private Scene scene;
	private ProjectScope projectScope;
	private Map<String, Object> editedAssets = new HashMap<>();

	public SceneEditorContext(SceneEditor editor) {
		editorId = editor.id;
		historyManager = editor.historyManager;

		editorInput = (IPathEditorInput) editor.getEditorInput();
		IResource resource = editor.getEditorInput().getAdapter(IResource.class);
		workspace = resource.getWorkspace();
		project = resource.getProject();
		javaProject = JavaCore.create(project);
		classLoader = DynamicURLClassLoader.newInstance(javaProject);
		Reflection.classResolver = classLoader::loadClass;
		EventService.subscribe(editorId, this);
	}

	@Override
	public void onEditorPreClose() {
		EventService.unsubscribe(editorId, this);
		unloadAssets();
		Optional.ofNullable(scene).ifPresent(s -> s.stop());
		String msg = "Error closing java project";
		Optional.ofNullable(javaProject).ifPresent(p -> Try.ofFailable(p, t -> t.close()).onFailure(e -> log(e, msg)));
	}

	private void unloadAssets() {
		for (Entry<String, Object> entry : editedAssets.entrySet()) {
			Object asset = entry.getValue();
			AssetService.unload(asset);
		}
	}

	void persistAssets(IProgressMonitor monitor) {
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

	public String getProjectStringPreference(String path, String name, String defaultValue) {
		Preferences preferences = getProjectScope().getNode(GurellaStudioPlugin.PLUGIN_ID);
		if (Values.isNotBlank(path)) {
			preferences = preferences.node(path);
		}

		return preferences.get(name, defaultValue);
	}

	public int getProjectIntPreference(String path, String name, int defaultValue) {
		Preferences preferences = getProjectScope().getNode(GurellaStudioPlugin.PLUGIN_ID);
		if (Values.isNotBlank(path)) {
			preferences = preferences.node(path);
		}

		return preferences.getInt(name, defaultValue);
	}

	public String getSceneStringPreference(String path, String name, String defaultValue) {
		Preferences preferences = getProjectScope().getNode(GurellaStudioPlugin.PLUGIN_ID);
		if (Values.isNotBlank(path)) {
			preferences = preferences.node(path);
		}

		return preferences.get(name, defaultValue);
	}

	public int getSceneIntPreference(String path, String name, int defaultValue) {
		IResource resource = editorInput.getAdapter(IResource.class);
		IPath resourcePath = resource.getProjectRelativePath();
		Preferences preferences = getProjectScope().getNode(GurellaStudioPlugin.PLUGIN_ID).node(resourcePath.toString());
		if (Values.isNotBlank(path)) {
			preferences = preferences.node(path);
		}
		
		return preferences.getInt(name, defaultValue);
	}

	public boolean getSceneBooleanPreference(String path, String name, boolean defaultValue) {
		IResource resource = editorInput.getAdapter(IResource.class);
		IPath resourcePath = resource.getProjectRelativePath();
		Preferences preferences = getProjectScope().getNode(GurellaStudioPlugin.PLUGIN_ID).node(resourcePath.toString());
		if (Values.isNotBlank(path)) {
			preferences = preferences.node(path);
		}
		
		return preferences.getBoolean(name, defaultValue);
	}
	
	public void setSceneIntPreference(String path, String name, int value) {
		IResource resource = editorInput.getAdapter(IResource.class);
		IPath resourcePath = resource.getProjectRelativePath();
		Preferences preferences = getProjectScope().getNode(GurellaStudioPlugin.PLUGIN_ID).node(resourcePath.toString());
		if (Values.isNotBlank(path)) {
			preferences = preferences.node(path);
		}
		
		preferences.putInt(name, value);
	}
	
	public void setSceneBooleanPreference(String path, String name, boolean value) {
		IResource resource = editorInput.getAdapter(IResource.class);
		IPath resourcePath = resource.getProjectRelativePath();
		Preferences preferences = getProjectScope().getNode(GurellaStudioPlugin.PLUGIN_ID).node(resourcePath.toString());
		if (Values.isNotBlank(path)) {
			preferences = preferences.node(path);
		}
		
		preferences.putBoolean(name, value);
	}

	public ProjectScope getProjectScope() {
		if (projectScope == null) {
			projectScope = new ProjectScope(project);
		}
		return projectScope;
	}
}
