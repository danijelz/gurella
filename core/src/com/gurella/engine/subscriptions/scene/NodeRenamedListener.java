package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.scene.SceneNode;

public interface NodeRenamedListener extends SceneEventSubscription {
	void onNodeRenamed(SceneNode node, String oldName, String newName);
}
