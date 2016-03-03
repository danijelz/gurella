package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.scene.SceneNodeComponent2;

//TODO unused
public interface ComponentActivityListener extends SceneEventSubscription {
	void componentActivated(SceneNodeComponent2 component);

	void componentDeactivated(SceneNodeComponent2 component);
}
