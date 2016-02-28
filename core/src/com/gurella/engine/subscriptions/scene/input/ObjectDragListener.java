package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.input.TouchEvent;

public interface ObjectDragListener extends EventSubscription {
	void onDragStart(IntersectionTouchEvent touchEvent);

	void onDragMove(TouchEvent touchEvent);

	void onDragEnd(TouchEvent touchEvent);
}
