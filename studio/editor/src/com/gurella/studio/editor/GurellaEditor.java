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
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class GurellaEditor extends EditorPart {
	private GLCanvas center;
	
	public GurellaEditor() {
		super();
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		SwtLwjglApplication application = new SwtLwjglApplication(new ApplicationListener() {
			@Override
			public void resume() {
			}

			@Override
			public void resize(int width, int height) {
			}

			@Override
			public void render() {
				Gdx.gl.glClearColor(0, 1, 0, 1);
				Gdx.gl.glClearStencil(0);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			}

			@Override
			public void pause() {
			}

			@Override
			public void dispose() {
			}

			@Override
			public void create() {
			}
		}, parent);
		
		
		center = application.getGraphics().getGlCanvas();
		center.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		IResource resource = getEditorInput().getAdapter(IResource.class);
		IProject project = resource.getProject();
		
		IJavaProject javaProject = JavaCore.create(project);
		IType lwType = null;
		IField[] fields = null;
		try {
			lwType = javaProject.findType("asdasdasd.TestDdd");
			fields = lwType.getFields();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
}
