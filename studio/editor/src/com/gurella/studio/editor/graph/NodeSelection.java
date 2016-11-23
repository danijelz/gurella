package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.StructuredSelection;

import com.gurella.engine.scene.SceneNode2;

public class NodeSelection extends StructuredSelection {
	NodeSelection(SceneNode2 component) {
		super(component);
	}

	public SceneNode2 getNode() {
		return (SceneNode2) getFirstElement();
	}
}
