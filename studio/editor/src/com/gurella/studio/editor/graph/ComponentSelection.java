package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.StructuredSelection;

import com.gurella.engine.scene.SceneNodeComponent2;

class ComponentSelection extends StructuredSelection {
	ComponentSelection(SceneNodeComponent2 component) {
		super(component);
	}

	SceneNodeComponent2 getComponent() {
		return (SceneNodeComponent2) getFirstElement();
	}
}
