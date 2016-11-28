package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.StructuredSelection;

import com.gurella.engine.scene.SceneElement;

abstract class ElementSelection extends StructuredSelection {
	ElementSelection(SceneElement element) {
		super(element);
	}

	SceneElement getElement() {
		return (SceneElement) getFirstElement();
	}
}
