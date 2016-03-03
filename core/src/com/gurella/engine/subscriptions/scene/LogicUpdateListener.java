package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.event.EventSubscription;

public interface LogicUpdateListener extends EventSubscription {
	void onLogicUpdate();
}
