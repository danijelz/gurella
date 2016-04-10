package com.gurella.studio.editor.scene;

import com.gurella.engine.scene.SceneNode2;
import com.gurella.studio.editor.SceneChangedMessage;

public class NodeNameChangedMessage implements SceneChangedMessage {
	SceneNode2 node;

	public NodeNameChangedMessage(SceneNode2 node) {
		this.node = node;
	}
}
