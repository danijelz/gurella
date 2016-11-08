package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.PointerInfo;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneTouchListener extends SceneEventSubscription {
	void onTouchDown(PointerInfo pointerInfo);

	void onTouchUp(PointerInfo pointerInfo);
}
