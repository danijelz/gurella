package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.StructuredSelection;

import com.gurella.engine.scene.SceneElement2;

abstract class ElementSelection extends StructuredSelection {
	ElementSelection(SceneElement2 element) {
		super(element);
	}

	SceneElement2 getElement() {
		return (SceneElement2) getFirstElement();
	}
}
