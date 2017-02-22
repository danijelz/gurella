package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneKeyListener extends SceneEventSubscription {
	void onKeyDown(int keycode);

	void onKeyUp(int keycode);
}
