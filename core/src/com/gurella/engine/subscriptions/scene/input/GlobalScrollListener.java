package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;

public interface GlobalScrollListener extends EventSubscription {
	void scrolled(int screenX, int screenY, int amount);
}
