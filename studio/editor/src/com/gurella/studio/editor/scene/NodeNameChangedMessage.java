package com.gurella.studio.editor.scene;

import com.gurella.engine.scene.SceneNode2;

public class NodeNameChangedMessage {
	SceneNode2 node;

	public NodeNameChangedMessage(SceneNode2 node) {
		this.node = node;
	}
}
