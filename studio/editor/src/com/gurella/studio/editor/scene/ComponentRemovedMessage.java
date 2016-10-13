package com.gurella.studio.editor.scene;

import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.SceneChangedMessage;

public class ComponentRemovedMessage implements SceneChangedMessage {
	public SceneNodeComponent2 component;

	public ComponentRemovedMessage(SceneNodeComponent2 component) {
		this.component = component;
	}
}
