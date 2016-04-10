package com.gurella.studio.editor.scene;

import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.SceneChangedMessage;

public class ComponentAddedMessage implements SceneChangedMessage {
	public SceneNodeComponent2 component;

	public ComponentAddedMessage(SceneNodeComponent2 component) {
		this.component = component;
	}
}
