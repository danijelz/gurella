package com.gurella.studio.editor.inspector.component;

import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.studio.editor.inspector.Inspectable;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;

public class ComponentInspectable implements Inspectable<SceneNodeComponent> {
	public final SceneNodeComponent target;

	public ComponentInspectable(SceneNodeComponent target) {
		this.target = target;
	}

	@Override
	public SceneNodeComponent getTarget() {
		return target;
	}

	@Override
	public InspectableContainer<SceneNodeComponent> createContainer(InspectorView parent,
			SceneNodeComponent target) {
		return new ComponentInspectableContainer(parent, target);
	}
}