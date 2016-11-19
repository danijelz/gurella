package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.StructuredSelection;

import com.gurella.engine.scene.SceneNode2;

class MoveNodeSelection extends StructuredSelection {
	MoveNodeSelection(SceneNode2 component) {
		super(component);
	}

	SceneNode2 getNode() {
		return (SceneNode2) getFirstElement();
	}
}
