package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.IntersectionTouchEvent;

public interface ObjectLongPressListener extends EventSubscription {
	void onLongPress(IntersectionTouchEvent touchEvent);
}
