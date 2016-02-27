package com.gurella.engine.subscriptions.application;

import com.gurella.engine.event.EventSubscription;

public interface ApplicationShutdownListener extends EventSubscription {
	void shutdown();
}
