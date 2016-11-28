package com.gurella.studio.editor.inspector.node;

import com.gurella.engine.scene.SceneNode;
import com.gurella.studio.editor.inspector.Inspectable;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;

public class NodeInspectable implements Inspectable<SceneNode> {
	public final SceneNode target;

	public NodeInspectable(SceneNode target) {
		this.target = target;
	}

	@Override
	public SceneNode getTarget() {
		return target;
	}

	@Override
	public InspectableContainer<SceneNode> createContainer(InspectorView parent, SceneNode target) {
		return new NodeInspectableContainer(parent, target);
	}
}