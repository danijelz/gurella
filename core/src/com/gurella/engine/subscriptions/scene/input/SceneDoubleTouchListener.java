package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.PointerInfo;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneDoubleTouchListener extends SceneEventSubscription {
	void onDoubleTouch(PointerInfo pointerInfo);
}
