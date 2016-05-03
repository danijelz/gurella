package com.gurella.studio.editor;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ui.IPathEditorInput;

import com.gurella.engine.application.ApplicationConfig;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.Reflection;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class GurellaSceneEditorContext {
	private IPathEditorInput editorInput;
	private IResource resource;
	private IWorkspace workspace;
	private IProject project;
	private IJavaProject javaProject;

	private URLClassLoader classLoader;

	private SwtLwjglApplication application;

	private EditorMessageSignal signal = new EditorMessageSignal();

	private ApplicationConfig applicationConfig;
	private Scene scene;
	
	private IContainer rootResource;

	public GurellaSceneEditorContext(IPathEditorInput editorInput) {
		this.editorInput = editorInput;
		resource = editorInput.getAdapter(IResource.class);
		workspace = resource.getWorkspace();
		project = resource.getProject();
		javaProject = JavaCore.create(project);
		createClassLoader();
	}

	private void createClassLoader() {
		try {
			String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
			List<URL> urlList = new ArrayList<URL>();
			for (int i = 0; i < classPathEntries.length; i++) {
				String entry = classPathEntries[i];
				IPath path = new Path(entry);
				URL url = path.toFile().toURI().toURL();
				urlList.add(url);
			}

			ClassLoader parentClassLoader = project.getClass().getClassLoader();
			URL[] urls = urlList.toArray(new URL[urlList.size()]);
			classLoader = new DynamicURLClassLoader(urls, parentClassLoader);
			Reflection.classResolver = classLoader::loadClass;
		} catch (MalformedURLException | CoreException e) {
			throw new RuntimeException("Can't create class loader.");
		}
	}

	private static class DynamicURLClassLoader extends URLClassLoader {
		public DynamicURLClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
			super(urls, parent, factory);
		}

		public DynamicURLClassLoader(URL[] urls, ClassLoader parent) {
			super(urls, parent);
		}

		public DynamicURLClassLoader(URL[] urls) {
			super(urls);
		}

		@Override
		public Class<?> loadClass(String className) throws ClassNotFoundException {
			try {
				return Platform.getBundle(GurellaStudioPlugin.PLUGIN_ID).loadClass(className);
			} catch (ClassNotFoundException exception) {
				return super.loadClass(className);
			}
		}
	}
}
