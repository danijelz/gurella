package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.scene.SceneNodeComponent;

public interface NodeComponentActivityListener extends NodeEventSubscription {
	void nodeComponentActivated(SceneNodeComponent component);

	void nodeComponentDeactivated(SceneNodeComponent component);
}
