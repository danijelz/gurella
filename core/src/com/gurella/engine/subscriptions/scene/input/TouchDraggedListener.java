package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.TouchEvent;

public interface TouchDraggedListener extends EventSubscription {
	void touchDragged(TouchEvent touchEvent);
}
