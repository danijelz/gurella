package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.scene.SceneNodeComponent;

public interface ComponentActivityListener extends SceneEventSubscription {
	void componentActivated(SceneNodeComponent component);

	void componentDeactivated(SceneNodeComponent component);
}
