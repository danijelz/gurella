package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneScrollListener extends SceneEventSubscription {
	void scrolled(int screenX, int screenY, int amount);
}
