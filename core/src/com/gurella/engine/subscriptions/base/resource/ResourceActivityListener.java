package com.gurella.engine.subscriptions.base.resource;

import com.gurella.engine.event.EventSubscription;

//TODO unused
public interface ResourceActivityListener extends EventSubscription {
	void resourceLoaded(String resourcePath);

	void resourceUnloaded(String resourcePath);

	void resourceReloaded(String resourcePath);
}
