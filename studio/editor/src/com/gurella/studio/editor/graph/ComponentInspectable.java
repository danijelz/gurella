package com.gurella.studio.editor.graph;

import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.inspector.Inspectable;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.inspector.component.ComponentInspectableContainer;

public class ComponentInspectable implements Inspectable<SceneNodeComponent2> {
	public final SceneNodeComponent2 target;

	public ComponentInspectable(SceneNodeComponent2 target) {
		this.target = target;
	}

	@Override
	public SceneNodeComponent2 getTarget() {
		return target;
	}

	@Override
	public InspectableContainer<SceneNodeComponent2> createContainer(InspectorView parent,
			SceneNodeComponent2 target) {
		return new ComponentInspectableContainer(parent, target);
	}
}