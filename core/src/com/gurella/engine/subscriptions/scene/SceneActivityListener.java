package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.event.EventSubscription;

public interface SceneActivityListener extends EventSubscription {
	void sceneStarted();

	void sceneStopped();
}
