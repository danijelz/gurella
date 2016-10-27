package com.gurella.studio.editor;

import static com.gurella.studio.GurellaStudioPlugin.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IPathEditorInput;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.Reflection;
import com.gurella.studio.editor.subscription.EditorClosingListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;
import com.gurella.studio.editor.utils.Try;

public class SceneEditorContext implements SceneLoadedListener, EditorClosingListener {
	public final int editorId;
	private final SceneEditorUndoContext undoContext;

	public final IPathEditorInput editorInput;
	public final IWorkspace workspace;
	public final IProject project;
	public final IJavaProject javaProject;
	// TODO make private and expose methods: loadClass(), createType()...
	public final ClassLoader classLoader;

	private Scene scene;

	Map<String, Object> editedAssets = new HashMap<>();

	public SceneEditorContext(SceneEditor editor) {
		editorId = editor.id;
		undoContext = editor.undoContext;

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
	public void closing() {
		EventService.unsubscribe(editorId, this);
		Optional.ofNullable(scene).ifPresent(s -> s.stop());
		String msg = "Error closing java project";
		Optional.ofNullable(javaProject).ifPresent(p -> Try.ofFailable(p, t -> t.close()).onFailure(e -> log(e, msg)));
	}

	public Scene getScene() {
		return scene;
	}

	@Override
	public void sceneLoaded(Scene scene) {
		this.scene = scene;
	}

	public void executeOperation(IUndoableOperation operation, String errorMsg) {
		undoContext.executeOperation(operation, errorMsg);
	}
}
