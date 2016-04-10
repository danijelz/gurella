package com.gurella.studio.editor;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.base.serialization.json.JsonOutput;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.utils.Reflection;
import com.gurella.studio.editor.assets.AssetsExplorerView;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.scene.SceneEditorMainContainer;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.scene.SceneHierarchyView;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class GurellaEditor extends EditorPart implements EditorMessageListener {
	private EditorMessageSignal signal = new EditorMessageSignal();
	private SceneEditorMainContainer mainContainer;

	List<SceneEditorView> registeredViews = new ArrayList<SceneEditorView>();

	private SwtLwjglApplication application;
	private Scene scene;
	boolean dirty;

	private IWorkspace workspace;
	private IProject project;
	private IJavaProject javaProject;

	private URLClassLoader classLoader;

	public GurellaEditor() {
		signal.addListener(this);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		IPath path = input.getFile().getFullPath();
		JsonOutput output = new JsonOutput();
		String string = output.serialize(Scene.class, scene);
		try {
			monitor.beginTask("Saving", 2000);
			ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
			manager.connect(path, LocationKind.IFILE, monitor);
			ITextFileBuffer buffer = ITextFileBufferManager.DEFAULT.getTextFileBuffer(path, LocationKind.IFILE);
			buffer.getDocument().set(new JsonReader().parse(string).prettyPrint(OutputType.minimal, 120));
			buffer.commit(monitor, true);
			manager.disconnect(path, LocationKind.IFILE, monitor);
			dirty = false;
			firePropertyChange(PROP_DIRTY);
		} catch (CoreException e) {
			// TODO Auto-generated method stub
			e.printStackTrace();
		} finally {
			monitor.done();
		}
	}

	public void addEditorMessageListener(EditorMessageListener listener) {
		signal.addListener(listener);
	}

	public void removeEditorMessageListener(EditorMessageListener listener) {
		signal.removeListener(listener);
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		workspace = ResourcesPlugin.getWorkspace();
		IResource resource = getEditorInput().getAdapter(IResource.class);
		project = resource.getProject();
		javaProject = JavaCore.create(project);

		mainContainer = new SceneEditorMainContainer(this, parent, SWT.NONE);
		mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		SceneHierarchyView sceneHierarchyView = new SceneHierarchyView(this, SWT.LEFT);
		registeredViews.add(sceneHierarchyView);
		AssetsExplorerView assetsExplorerView = new AssetsExplorerView(this, SWT.LEFT);
		registeredViews.add(assetsExplorerView);
		InspectorView inspectorView = new InspectorView(this, SWT.RIGHT);
		registeredViews.add(inspectorView);

		mainContainer.setSelection(sceneHierarchyView);

		Composite center = mainContainer.getCenter();
		application = new SwtLwjglApplication(new SceneEditorApplicationAdapter(), center);

		createClassLoader();
		Reflection.classResolver = classLoader::loadClass;

		IPathEditorInput pathEditorInput = (IPathEditorInput) getEditorInput();
		ResourceService.loadAsync(pathEditorInput.getPath().toString(), Scene.class, new AsyncCallback<Scene>() {
			@Override
			public void onSuccess(Scene scene) {
				presentScene(scene);
			}

			@Override
			public void onException(Throwable exception) {
				// TODO Auto-generated method stub
				exception.printStackTrace();
			}

			@Override
			public void onCancled(String message) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgress(float progress) {
				// TODO Auto-generated method stub
			}
		}, 0);
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
		} catch (MalformedURLException | CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void presentScene(Scene scene) {
		this.scene = scene;
		dirty = false;
		postMessage(null, new SceneLoadedMessage(scene));
	}

	public Scene getScene() {
		return scene;
	}

	public SceneEditorMainContainer getMainContainer() {
		return mainContainer;
	}

	public IWorkspace getWorkspace() {
		return workspace;
	}

	public IProject getProject() {
		return project;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public URLClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		super.dispose();
		ResourceService.unload(scene);
		signal.clear();
		if (application != null) {
			application.exit();
		}
		if (javaProject != null) {
			try {
				javaProject.close();
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void postMessage(Object source, Object message) {
		signal.dispatch(source, message);
		for (SceneEditorView view : registeredViews) {
			if (source != view) {
				view.handleMessage(source, message);
			}
		}
	}

	@Override
	public void handleMessage(Object source, Object message) {
		if (message instanceof SceneChangedMessage) {
			dirty = true;
			firePropertyChange(PROP_DIRTY);
		}
	}

	private static final class SceneEditorApplicationAdapter extends ApplicationAdapter {
		@Override
		public void render() {
			Array<ApplicationUpdateListener> listeners = new Array<ApplicationUpdateListener>();
			EventService.getSubscribers(ApplicationUpdateListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).update();
			}
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClearStencil(0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
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
