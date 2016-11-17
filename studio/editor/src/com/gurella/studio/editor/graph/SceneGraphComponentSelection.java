package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.engine.scene.SceneNodeComponent2;

public class SceneGraphComponentSelection extends StructuredSelection {
	TreeItem item;

	SceneGraphComponentSelection(TreeItem item, SceneNodeComponent2 component) {
		super(component);
		this.item = item;
	}

	public SceneNodeComponent2 getComponent() {
		return (SceneNodeComponent2) getFirstElement();
	}
}
