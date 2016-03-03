package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.input.TouchEvent;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeDragOverListener extends NodeEventSubscription {
	void onDragOverStart(IntersectionTouchEvent touchEvent);

	void onDragOverMove(IntersectionTouchEvent touchEvent);

	void onDragOverEnd(TouchEvent touchEvent);
}
