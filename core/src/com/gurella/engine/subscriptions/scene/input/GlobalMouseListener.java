package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;

public interface GlobalMouseListener extends EventSubscription {
	void mouseMoved(int screenX, int screenY);
}
