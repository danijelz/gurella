package com.gurella.engine.subscriptions.scene;

public interface SceneActivityListener extends SceneEventSubscription {
	void sceneStarted();

	void sceneStopped();
}
