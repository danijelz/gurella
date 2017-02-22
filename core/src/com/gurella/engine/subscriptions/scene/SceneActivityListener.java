package com.gurella.engine.subscriptions.scene;

public interface SceneActivityListener extends SceneEventSubscription {
	void onSceneStarted();

	void onSceneStopped();
}
