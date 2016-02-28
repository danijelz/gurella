package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;

public interface KeyListener extends EventSubscription {
	void keyDown(int keycode);

	void keyUp(int keycode);
}
