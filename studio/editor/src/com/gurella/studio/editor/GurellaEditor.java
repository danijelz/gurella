package com.gurella.studio.editor;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
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
import com.gurella.engine.base.resource.JsonArchiveLoader;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.base.serialization.json.JsonOutput;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.studio.editor.scene.SceneEditorMainContainer;
import com.gurella.studio.editor.scene.SceneGraphView;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class GurellaEditor extends EditorPart {
	private SwtLwjglApplication application;

	private SceneEditorMainContainer mainContainer;

	private SceneGraphView sceneGraphView;

	private Scene scene;

	public GurellaEditor() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		IPathEditorInput pathEditorInput = (IPathEditorInput) getEditorInput();
		IPath path = pathEditorInput.getPath();
		JsonOutput output = new JsonOutput();
		String string = output.serialize(Scene.class, scene);
		try {
			monitor.beginTask("", 2000);
			ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
			manager.connect(path, LocationKind.IFILE, monitor);
			ITextFileBuffer buffer = ITextFileBufferManager.DEFAULT.getTextFileBuffer(path, LocationKind.IFILE);
			buffer.getDocument().set(new JsonReader().parse(string).prettyPrint(OutputType.minimal, 120));
			buffer.commit(monitor, true);
			manager.disconnect(path, LocationKind.IFILE, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			monitor.done();
		}

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		mainContainer = new SceneEditorMainContainer(parent, SWT.NONE);
		mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sceneGraphView = new SceneGraphView(mainContainer, SWT.RIGHT);
		Composite center = mainContainer.getCenter();
		application = new SwtLwjglApplication(new SceneEditorApplicationAdapter(), center);

		IResource resource = getEditorInput().getAdapter(IResource.class);
		IProject project = resource.getProject();

		IJavaProject javaProject = JavaCore.create(project);
		try {
			IType lwType = javaProject.findType("java.util.ArrayList");
			IField[] fields = lwType.getFields();
			for (IField iField : fields) {
				iField.getElementName();
			}
		} catch (JavaModelException e) {
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
		System.out.println("loaded");
		sceneGraphView.present(scene);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		super.dispose();
		//TODO center.dispose();
	}

	private final class SceneEditorApplicationAdapter extends ApplicationAdapter {
		@Override
		public void render() {
			Array<ApplicationUpdateListener> listeners = new Array<ApplicationUpdateListener>();
			EventService.getSubscribers(ApplicationUpdateListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).update();
			}
			Gdx.gl.glClearColor(0, 1, 0, 1);
			Gdx.gl.glClearStencil(0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		}
	}
}
