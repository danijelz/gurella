package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.TouchEvent;

public interface GlobalDoubleTouchDownListener extends EventSubscription {
	void doubleTouchDown(TouchEvent touchEvent);
}
