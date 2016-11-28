package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.scene.SceneNode;

public interface NodeRenamedListener extends SceneEventSubscription {
	void nodeRenamed(SceneNode node, String oldName, String newName);
}
