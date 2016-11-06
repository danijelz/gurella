package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.TouchInfo;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeDragListener extends NodeEventSubscription {
	void onDragStart(TouchInfo touchInfo);

	void onDragMove(TouchInfo touchInfo);

	void onDragEnd(TouchInfo touchInfo);
}
