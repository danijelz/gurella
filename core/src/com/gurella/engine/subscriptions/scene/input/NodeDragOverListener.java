package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.DragInfo;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeDragOverListener extends NodeEventSubscription {
	void onDragOverStart(DragInfo dragInfo);

	void onDragOverMove(DragInfo dragInfo);

	void onDragOverEnd(DragInfo dragInfo);
}
