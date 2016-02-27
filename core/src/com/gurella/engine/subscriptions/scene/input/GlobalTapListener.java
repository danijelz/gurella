package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.TouchEvent;

public interface GlobalTapListener extends EventSubscription {
	void tap(TouchEvent touchEvent, int count);
}
