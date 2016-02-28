package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.input.TouchEvent;

public interface ObjectOverDragListener extends EventSubscription {
	void onDragOverStart(IntersectionTouchEvent touchEvent);

	void onDragOverMove(IntersectionTouchEvent touchEvent);

	void onDragOverEnd(TouchEvent touchEvent);
}
