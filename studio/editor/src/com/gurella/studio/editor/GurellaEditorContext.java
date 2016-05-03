package com.gurella.studio.editor;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IPathEditorInput;

import com.gurella.engine.application.ApplicationConfig;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.Reflection;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class GurellaEditorContext {
	public final IPathEditorInput editorInput;
	public final IResource resource;
	public final IWorkspace workspace;
	public final IProject project;
	public final IJavaProject javaProject;

	public final URLClassLoader classLoader;

	private SwtLwjglApplication application;

	private EditorMessageSignal signal = new EditorMessageSignal();
	private List<SceneEditorView> registeredViews = new ArrayList<SceneEditorView>();

	private ApplicationConfig applicationConfig;
	private Scene scene;

	private IContainer rootResource;

	public GurellaEditorContext(IPathEditorInput editorInput) {
		this.editorInput = editorInput;
		resource = editorInput.getAdapter(IResource.class);
		workspace = resource.getWorkspace();
		project = resource.getProject();
		javaProject = JavaCore.create(project);
		classLoader = DynamicURLClassLoader.newInstance(javaProject);
		Reflection.classResolver = classLoader::loadClass;
	}
}
