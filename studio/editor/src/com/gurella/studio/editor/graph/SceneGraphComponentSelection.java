package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.StructuredSelection;

import com.gurella.engine.scene.SceneNodeComponent2;

public class SceneGraphComponentSelection extends StructuredSelection {

	SceneGraphComponentSelection(SceneNodeComponent2 component) {
		super(component);
	}

	public SceneNodeComponent2 getComponent() {
		return (SceneNodeComponent2) getFirstElement();
	}
}
