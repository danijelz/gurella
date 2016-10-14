package com.gurella.studio.editor.inspector.pixmap;

import org.eclipse.core.resources.IFile;

import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.inspector.InspectorView.Inspectable;

public class PixmapInspectable implements Inspectable<IFile> {
	IFile target;

	public PixmapInspectable(IFile file) {
		this.target = file;
	}

	@Override
	public IFile getTarget() {
		return target;
	}

	@Override
	public InspectableContainer<IFile> createContainer(InspectorView parent, IFile target) {
		return new PixmapInspectableContainer(parent, target);
	}
}