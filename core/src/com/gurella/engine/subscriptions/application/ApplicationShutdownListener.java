package com.gurella.engine.subscriptions.application;

public interface ApplicationShutdownListener extends ApplicationEventSubscription {
	void onShutdown();
}
