package com.gurella.studio.editor;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.resource.DeviceResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
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
import com.gurella.studio.editor.scene.InspectorView;
import com.gurella.studio.editor.scene.ProjectExplorerView;
import com.gurella.studio.editor.scene.SceneEditorMainContainer;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.scene.SceneHierarchyView;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class GurellaEditor extends EditorPart {
	private SwtLwjglApplication application;

	private SceneEditorMainContainer mainContainer;
	private SceneHierarchyView sceneHierarchyView;
	private InspectorView inspectorView;
	private ProjectExplorerView projectExplorerView;
	private List<SceneEditorView> registeredViews = new ArrayList<SceneEditorView>();

	private DeviceResourceManager resourceManager;
	private FormToolkit toolkit;

	private Scene scene;
	boolean dirty;

	private IProject project;
	private IJavaProject javaProject;

	public GurellaEditor() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
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
			e.printStackTrace();
		} finally {
			monitor.done();
		}
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
		toolkit = new FormToolkit(parent.getDisplay());
		resourceManager = new DeviceResourceManager(parent.getDisplay());

		IResource resource = getEditorInput().getAdapter(IResource.class);
		project = resource.getProject();
		javaProject = JavaCore.create(project);

		mainContainer = new SceneEditorMainContainer(parent, SWT.NONE);
		mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		sceneHierarchyView = new SceneHierarchyView(this, SWT.LEFT);
		registeredViews.add(sceneHierarchyView);
		projectExplorerView = new ProjectExplorerView(this, SWT.LEFT);
		registeredViews.add(projectExplorerView);
		inspectorView = new InspectorView(this, SWT.RIGHT);
		registeredViews.add(inspectorView);

		mainContainer.setSelection(sceneHierarchyView);

		Composite center = mainContainer.getCenter();
		application = new SwtLwjglApplication(new SceneEditorApplicationAdapter(), center);

		try {
			/*
			 * IPackageFragmentRoot[] roots = javaProject.getAllPackageFragmentRoots(); IPackageFragmentRoot root =
			 * roots[0];
			 */

			String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
			List<URL> urlList = new ArrayList<URL>();
			for (int i = 0; i < classPathEntries.length; i++) {
				String entry = classPathEntries[i];
				IPath path = new Path(entry);
				URL url = path.toFile().toURI().toURL();
				urlList.add(url);
			}

			JavaCompiler compiler = new JavaCompilerFactory().createCompiler("eclipse");
			// CompilationResult result = compiler.compile(null, new
			// FileResourceReader(root.getCorrespondingResource().getFullPath().toFile()), new MemoryResourceStore());
			ClassLoader parentClassLoader = project.getClass().getClassLoader();
			URL[] urls = urlList.toArray(new URL[urlList.size()]);
			URLClassLoader classLoader = new URLClassLoader(urls, parentClassLoader);
			// classLoader.loadClass("test.Test");
			IType lwType = javaProject.findType("test.Test");

			// Class.forName("test.Test")
			IField[] fields = lwType.getFields();
			for (IField iField : fields) {
				iField.getElementName();
			}
		} catch (MalformedURLException | CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	private void presentScene(Scene scene) {
		this.scene = scene;
		dirty = false;
		sceneHierarchyView.present(scene);
	}

	public FormToolkit getToolkit() {
		return toolkit;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public SceneEditorMainContainer getMainContainer() {
		return mainContainer;
	}

	public IProject getProject() {
		return project;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		super.dispose();
		ResourceService.unload(scene);
		toolkit.dispose();
		resourceManager.dispose();
		application.exit();
	}

	private final class SceneEditorApplicationAdapter extends ApplicationAdapter {
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

	public void setDirty() {
		dirty = true;
		firePropertyChange(PROP_DIRTY);
	}

	public void postMessage(SceneEditorView source, Object message, Object[] additionalData) {
		for (SceneEditorView view : registeredViews) {
			if (source != view) {
				view.handleMessage(source, message, additionalData);
			}
		}
	}
}
