package com.gurella.studio.editor.inspector.bitmapfont;

import org.eclipse.core.resources.IFile;

import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.inspector.InspectorView.Inspectable;

public class BitmapFontInspectable implements Inspectable<IFile> {
	IFile target;

	public BitmapFontInspectable(IFile file) {
		this.target = file;
	}

	@Override
	public IFile getTarget() {
		return target;
	}

	@Override
	public InspectableContainer<IFile> createContainer(InspectorView parent, IFile target) {
		return new BitmapFontInspectableContainer(parent, target);
	}
}
