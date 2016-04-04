package com.gurella.studio.editor.scene;

import com.gurella.engine.scene.SceneNodeComponent2;

public class ComponentAddedMessage {
	public SceneNodeComponent2 component;

	public ComponentAddedMessage(SceneNodeComponent2 component) {
		this.component = component;
	}
}
