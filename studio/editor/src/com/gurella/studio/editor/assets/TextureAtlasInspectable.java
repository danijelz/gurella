package com.gurella.studio.editor.assets;

import org.eclipse.core.resources.IFile;

import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.inspector.TextureAtlasInspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView.Inspectable;

public class TextureAtlasInspectable implements Inspectable<IFile> {
	IFile target;

	public TextureAtlasInspectable(IFile file) {
		this.target = file;
	}

	@Override
	public IFile getTarget() {
		return target;
	}

	@Override
	public InspectableContainer<IFile> createContainer(InspectorView parent, IFile target) {
		return new TextureAtlasInspectableContainer(parent, target);
	}
}