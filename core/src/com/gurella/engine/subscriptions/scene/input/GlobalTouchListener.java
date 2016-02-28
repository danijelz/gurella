package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.TouchEvent;

public interface GlobalTouchListener extends EventSubscription {
	void touchDown(TouchEvent touchEvent);

	void touchUp(TouchEvent touchEvent);
}
