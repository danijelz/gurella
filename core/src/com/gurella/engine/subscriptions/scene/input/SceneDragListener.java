package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.DragInfo;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneDragListener extends SceneEventSubscription {
	void onDragStart(DragInfo dragInfo);//TODO handle in processor

	void onDragged(DragInfo dragInfo);

	void onDragEnd(DragInfo dragInfo);//TODO handle in processor
}
