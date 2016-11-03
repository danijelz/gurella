package com.gurella.studio.editor.tool;

import org.eclipse.core.commands.operations.AbstractOperation;

import com.gurella.engine.scene.transform.TransformComponent;

abstract class TransformOperation extends AbstractOperation {
	final int editorId;
	final TransformComponent component;

	public TransformOperation(String label, int editorId, TransformComponent component) {
		super(label);
		this.editorId = editorId;
		this.component = component;
	}

	abstract void rollback();
	
	abstract void commit();
}
