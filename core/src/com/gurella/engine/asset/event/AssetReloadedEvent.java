package com.gurella.engine.asset.event;

import com.gurella.engine.asset.AssetId;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.asset.AssetActivityListener;

public class AssetReloadedEvent implements Event<AssetActivityListener> {
	AssetId assetId;
	Object asset;

	void set(AssetId assetId, Object asset) {
		this.assetId = assetId;
		this.asset = asset;
		EventService.post(AssetActivityListener.class, this);
		this.assetId = null;
		this.asset = null;
	}

	@Override
	public void dispatch(AssetActivityListener listener) {
		listener.onAssetReloaded(assetId, asset);
	}

	@Override
	public Class<AssetActivityListener> getSubscriptionType() {
		return AssetActivityListener.class;
	}
}
