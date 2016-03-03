package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.scene.SceneNodeComponent2;

public interface NodeComponentActivityListener extends SceneEventSubscription {
	void nodeComponentActivated(SceneNodeComponent2 component);

	void nodeComponentDeactivated(SceneNodeComponent2 component);
}
