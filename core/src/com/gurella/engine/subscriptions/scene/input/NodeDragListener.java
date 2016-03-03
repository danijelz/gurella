package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.input.TouchEvent;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeDragListener extends NodeEventSubscription {
	void onDragStart(IntersectionTouchEvent touchEvent);

	void onDragMove(TouchEvent touchEvent);

	void onDragEnd(TouchEvent touchEvent);
}
