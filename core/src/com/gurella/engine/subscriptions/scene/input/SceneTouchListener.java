package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.TouchInfo;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneTouchListener extends SceneEventSubscription {
	void onTouchDown(TouchInfo touchInfo);

	void onTouchUp(TouchInfo touchInfo);
}
