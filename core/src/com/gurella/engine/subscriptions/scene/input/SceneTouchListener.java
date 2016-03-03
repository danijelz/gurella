package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.TouchEvent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneTouchListener extends SceneEventSubscription {
	void touchDown(TouchEvent touchEvent);

	void touchUp(TouchEvent touchEvent);
}
