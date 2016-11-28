package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.StructuredSelection;

import com.gurella.engine.scene.SceneNodeComponent;

class ComponentSelection extends StructuredSelection {
	ComponentSelection(SceneNodeComponent component) {
		super(component);
	}

	SceneNodeComponent getComponent() {
		return (SceneNodeComponent) getFirstElement();
	}
}
