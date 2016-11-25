package com.gurella.engine.subscriptions.base.resource;

import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface AssetActivityListener extends ApplicationEventSubscription {
	void assetLoaded(String fileName, Object asset);

	void assetUnloaded(String fileName, Object asset);

	void assetReloaded(String fileName, Object asset);
}