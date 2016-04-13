package com.gurella.studio.editor.inspector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.model.ModelEditorContainer;
import com.gurella.studio.editor.utils.FolderRelativeFileHandleResolver;

public class ModelInspectableContainer extends InspectableContainer<IFile> {
	private ModelEditorContainer<Object> textureProperties;
	private GLCanvas glCanvas;
	
	public ModelInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		Composite body = getBody();
		body.setLayout(new GridLayout(2, false));
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		ModelLoader<?> loader = new ObjLoader(new FolderRelativeFileHandleResolver((IFolder) target.getParent()));
		FileHandle fileHandle = new FileHandle(target.getLocation().toFile());
		ModelData modelData = loader.loadModelData(fileHandle);
		modelData.toString();
		
		GLData glData = new GLData();
		glData.redSize = 8;
		glData.greenSize = 8;
		glData.blueSize = 8;
		glData.alphaSize = 8;
		glData.depthSize = 16;
		glData.stencilSize = 0;
		glData.samples = 0;
		glData.doubleBuffer = true; //TODO
		
		glCanvas = new GLCanvas(body, SWT.FLAT, glData);
		glCanvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// TODO Auto-generated constructor stub
	}

}
