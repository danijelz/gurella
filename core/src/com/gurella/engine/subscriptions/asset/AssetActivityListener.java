package com.gurella.engine.subscriptions.asset;

import com.gurella.engine.asset.AssetId;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface AssetActivityListener extends ApplicationEventSubscription {
	void assetLoaded(AssetId assetId, Object asset);

	void assetUnloaded(AssetId assetId, Object asset);

	void assetReloaded(AssetId assetId, Object asset);
}
