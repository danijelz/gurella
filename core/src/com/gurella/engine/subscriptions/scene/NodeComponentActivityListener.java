package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.scene.SceneNodeComponent;

public interface NodeComponentActivityListener extends NodeEventSubscription {
	void onNodeComponentActivated(SceneNodeComponent component);

	void onNodeComponentDeactivated(SceneNodeComponent component);
}
