package com.gurella.engine.subscriptions.application;

import com.gurella.engine.event.EventSubscription;

public interface ApplicationUpdateListener extends EventSubscription {
	void update();
}
