package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.StructuredSelection;

import com.gurella.engine.scene.SceneNode;

public class NodeSelection extends StructuredSelection {
	NodeSelection(SceneNode component) {
		super(component);
	}

	public SceneNode getNode() {
		return (SceneNode) getFirstElement();
	}
}
