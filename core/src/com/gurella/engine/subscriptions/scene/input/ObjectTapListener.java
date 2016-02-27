package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.IntersectionTouchEvent;

public interface ObjectTapListener extends EventSubscription {
	void onTap(IntersectionTouchEvent touchEvent, int count);
}
