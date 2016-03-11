package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.scene.SceneNode2;

public interface NodeRenamedListener extends SceneEventSubscription {
	void nodeRenamed(SceneNode2 node, String oldName, String newName);
}
