package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneKeyTypedListener extends SceneEventSubscription {
	void onKeyTyped(char character);
}
