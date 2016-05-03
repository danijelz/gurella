package com.gurella.studio.editor;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;

import com.gurella.studio.GurellaStudioPlugin;

class DynamicURLClassLoader extends URLClassLoader {
	public static DynamicURLClassLoader newInstance(IJavaProject javaProject) {
		try {
			String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
			List<URL> urlList = new ArrayList<URL>();
			for (int i = 0; i < classPathEntries.length; i++) {
				String entry = classPathEntries[i];
				IPath path = new Path(entry);
				URL url = path.toFile().toURI().toURL();
				urlList.add(url);
			}

			ClassLoader parentClassLoader = javaProject.getClass().getClassLoader();
			URL[] urls = urlList.toArray(new URL[urlList.size()]);
			return new DynamicURLClassLoader(urls, parentClassLoader);
		} catch (MalformedURLException | CoreException e) {
			throw new RuntimeException("Can't create class loader.");
		}
	}
	
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