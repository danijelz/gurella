package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.StructuredSelection;

import com.gurella.engine.scene.SceneElement2;

class CopyElementSelection extends StructuredSelection {
	CopyElementSelection(SceneElement2 element) {
		super(element);
	}

	SceneElement2 getElement() {
		return (SceneElement2) getFirstElement();
	}
}
