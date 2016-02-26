package com.gurella.engine.subscriptions.application;

import com.gurella.engine.event.EventSubscription;

public interface ApplicationShutdownSignal extends EventSubscription {
	void onShutdown();
}
