package com.gurella.studio.editor.tool;

import org.eclipse.core.commands.operations.AbstractOperation;

import com.gurella.engine.scene.transform.TransformComponent;

abstract class TransformOperation extends AbstractOperation {
	final int editorId;
	final TransformComponent transform;

	public TransformOperation(String label, int editorId, TransformComponent transform) {
		super(label);
		this.editorId = editorId;
		this.transform = transform;
	}

	abstract void rollback();
	
	abstract void commit();
}
