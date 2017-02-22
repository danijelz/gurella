package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.scene.SceneNodeComponent;

public interface ComponentActivityListener extends SceneEventSubscription {
	void onComponentActivated(SceneNodeComponent component);

	void onComponentDeactivated(SceneNodeComponent component);
}
