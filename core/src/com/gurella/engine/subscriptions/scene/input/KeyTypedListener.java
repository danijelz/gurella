package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;

public interface KeyTypedListener extends EventSubscription {
	void keyTyped(char character);
}
