package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.TouchInfo;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneTouchDraggedListener extends SceneEventSubscription {
	void touchDragged(TouchInfo touchInfo);
}
