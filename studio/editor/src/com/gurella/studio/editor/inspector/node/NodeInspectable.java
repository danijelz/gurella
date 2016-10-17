package com.gurella.studio.editor.inspector.node;

import com.gurella.engine.scene.SceneNode2;
import com.gurella.studio.editor.inspector.Inspectable;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;

public class NodeInspectable implements Inspectable<SceneNode2> {
	public final SceneNode2 target;

	public NodeInspectable(SceneNode2 target) {
		this.target = target;
	}

	@Override
	public SceneNode2 getTarget() {
		return target;
	}

	@Override
	public InspectableContainer<SceneNode2> createContainer(InspectorView parent, SceneNode2 target) {
		return new NodeInspectableContainer(parent, target);
	}
}