package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneKeyListener extends SceneEventSubscription {
	void keyDown(int keycode);

	void keyUp(int keycode);
}
