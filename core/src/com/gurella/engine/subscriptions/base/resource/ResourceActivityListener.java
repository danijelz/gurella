package com.gurella.engine.subscriptions.base.resource;

import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ResourceActivityListener extends ApplicationEventSubscription {
	void resourceLoaded(String resourcePath, Object resource);

	void resourceUnloaded(String resourcePath);

	void resourceReloaded(String resourcePath);
}
