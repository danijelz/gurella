package com.gurella.studio.editor;

import java.net.URLClassLoader;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jdt.core.IJavaProject;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.gurella.engine.scene.Scene;
import com.gurella.studio.editor.scene.SceneRenderer;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class GurellaSceneEditorContext {
	private SwtLwjglApplication application;
	private SceneRenderer sceneRenderer;
	private ModelBatch modelBatch;
	private Scene scene;

	private IWorkspace workspace;
	private IProject project;
	private IJavaProject javaProject;

	private URLClassLoader classLoader;
	
	private EditorMessageSignal signal = new EditorMessageSignal();
}
