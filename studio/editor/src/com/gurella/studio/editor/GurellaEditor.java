package com.gurella.studio.editor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.base.serialization.json.JsonInput;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.studio.editor.scene.SceneEditorMainContainer;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class GurellaEditor extends EditorPart {
	private SwtLwjglApplication application;
	private SceneEditorMainContainer mainContainer;

	public GurellaEditor() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
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
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		mainContainer = new SceneEditorMainContainer(parent, SWT.NONE);
		mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite center = mainContainer.getCenter();
		center.setLayout(new GridLayout());
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
		System.out.println("loaded");
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

		@Override
		public void dispose() {
		}

		@Override
		public void create() {
		}
	}
}
