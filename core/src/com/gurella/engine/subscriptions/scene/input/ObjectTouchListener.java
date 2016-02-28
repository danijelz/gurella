package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.IntersectionTouchEvent;

public interface ObjectTouchListener extends EventSubscription {
	void onTouchDown(IntersectionTouchEvent touchEvent);

	void onTouchUp(IntersectionTouchEvent touchEvent);
}
