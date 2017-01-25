package com.gurella.engine.subscriptions.application;

public interface ApplicationCleanupListener extends ApplicationEventSubscription {
	void cleanup();
}
