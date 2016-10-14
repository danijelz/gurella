package com.gurella.studio.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IPathEditorInput;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.Reflection;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.part.SceneEditorView;
import com.gurella.studio.editor.subscription.SceneLoadedListener;

public class SceneEditorContext implements SceneLoadedListener {
	public final int editorId;
	private final SceneEditor editor;
	public final IPathEditorInput editorInput;
	public final IResource editorInputResource;
	public final IWorkspace workspace;
	public final IProject project;
	public final IJavaProject javaProject;

	public final ClassLoader classLoader;

	private List<SceneEditorView> registeredViews = new ArrayList<SceneEditorView>();

	Scene scene;

	public SceneEditorContext(SceneEditor editor) {
		this.editor = editor;
		editorId = editor.id;
		editorInput = (IPathEditorInput) editor.getEditorInput();
		editorInputResource = editor.getEditorInput().getAdapter(IResource.class);
		workspace = editorInputResource.getWorkspace();
		project = editorInputResource.getProject();
		javaProject = JavaCore.create(project);
		classLoader = DynamicURLClassLoader.newInstance(javaProject);
		Reflection.classResolver = classLoader::loadClass;

		EventService.subscribe(editorId, this);
	}

	void dispose() {
		scene.stop();
		closeJavaProject();
		EventService.unsubscribe(editorId, this);
	}

	private void closeJavaProject() {
		if (javaProject != null) {
			try {
				javaProject.close();
			} catch (JavaModelException e) {
				GurellaStudioPlugin.log(e, "Error cloasing java project");
			}
		}
	}

	public Scene getScene() {
		return scene;
	}

	@Override
	public void sceneLoaded(Scene scene) {
		this.scene = scene;
		scene.start();
	}

	public void executeOperation(IUndoableOperation operation, String errorMsg) {
		operation.addContext(editor.undoContext);
		try {
			editor.operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			GurellaStudioPlugin.showError(e, errorMsg);
		}
	}
}
