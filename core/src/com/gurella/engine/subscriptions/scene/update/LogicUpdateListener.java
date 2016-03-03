package com.gurella.engine.subscriptions.scene.update;

import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface LogicUpdateListener extends SceneEventSubscription {
	void onLogicUpdate();
}
