package com.gurella.studio.editor.inspector.material;

import org.eclipse.core.resources.IFile;

import com.gurella.studio.editor.inspector.Inspectable;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;

public class MaterialInspectable implements Inspectable<IFile> {
	IFile target;

	public MaterialInspectable(IFile file) {
		this.target = file;
	}

	@Override
	public IFile getTarget() {
		return target;
	}

	@Override
	public InspectableContainer<IFile> createControl(InspectorView parent, IFile target) {
		return new MaterialInspectableContainer(parent, target);
	}
}
