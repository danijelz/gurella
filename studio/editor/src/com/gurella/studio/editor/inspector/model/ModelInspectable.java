package com.gurella.studio.editor.inspector.model;

import org.eclipse.core.resources.IFile;

import com.gurella.studio.editor.inspector.Inspectable;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;

public class ModelInspectable implements Inspectable<IFile> {
	IFile target;

	public ModelInspectable(IFile file) {
		this.target = file;
	}

	@Override
	public IFile getTarget() {
		return target;
	}

	@Override
	public InspectableContainer<IFile> createControl(InspectorView parent, IFile target) {
		return new ModelInspectableContainer(parent, target);
	}
}
