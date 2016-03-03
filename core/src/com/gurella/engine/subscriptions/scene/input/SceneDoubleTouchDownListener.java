package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.TouchEvent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneDoubleTouchDownListener extends SceneEventSubscription {
	void doubleTouchDown(TouchEvent touchEvent);
}
