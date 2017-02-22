package com.gurella.engine.subscriptions.asset;

import com.gurella.engine.asset.AssetId;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface AssetActivityListener extends ApplicationEventSubscription {
	void onAssetLoaded(AssetId assetId, Object asset);

	void onAssetUnloaded(AssetId assetId, Object asset);

	void onAssetReloaded(AssetId assetId, Object asset);
}
