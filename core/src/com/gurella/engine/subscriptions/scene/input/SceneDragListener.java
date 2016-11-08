package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.PointerInfo;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneDragListener extends SceneEventSubscription {
	void onDragStart(PointerInfo pointerInfo);

	void onDragged(PointerInfo pointerInfo);

	void onDragEnd(PointerInfo pointerInfo);
}
