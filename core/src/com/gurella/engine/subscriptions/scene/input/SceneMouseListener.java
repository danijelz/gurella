package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneMouseListener extends SceneEventSubscription {
	void mouseMoved(int screenX, int screenY);
}
