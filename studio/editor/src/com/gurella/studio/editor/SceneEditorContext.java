package com.gurella.studio.editor;

import java.net.URLClassLoader;
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

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.Reflection;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.scene.event.SceneLoadedEvent;

public class SceneEditorContext {
	public final int editorId;
	private final GurellaSceneEditor editor;
	public final IResource editorInputResource;
	public final IWorkspace workspace;
	public final IProject project;
	public final IJavaProject javaProject;

	public final URLClassLoader classLoader;

	private List<SceneEditorView> registeredViews = new ArrayList<SceneEditorView>();

	Scene scene;

	public SceneEditorContext(GurellaSceneEditor editor) {
		this.editor = editor;
		editorId = editor.id;
		editorInputResource = editor.getEditorInput().getAdapter(IResource.class);
		workspace = editorInputResource.getWorkspace();
		project = editorInputResource.getProject();
		javaProject = JavaCore.create(project);
		classLoader = DynamicURLClassLoader.newInstance(javaProject);
		Reflection.classResolver = classLoader::loadClass;
	}

	void dispose() {
		scene.stop();
		closeJavaProject();
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

	void setScene(Scene scene) {
		this.scene = scene;
		scene.start();
		EventService.post(editor.id, new SceneLoadedEvent(scene));
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
