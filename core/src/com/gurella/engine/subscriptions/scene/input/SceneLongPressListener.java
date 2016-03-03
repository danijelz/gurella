package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.TouchEvent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneLongPressListener extends SceneEventSubscription {
	void longPress(TouchEvent touchEvent);
}
