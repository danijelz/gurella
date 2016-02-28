package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.Scene;

public interface SceneActivityListener extends EventSubscription {
	void sceneStared(Scene scene);

	void sceneStopped(Scene scene);
}
