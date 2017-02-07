package com.gurella.engine.asset.event;

import com.gurella.engine.asset.AssetId;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.asset.AssetActivityListener;

public class AssetUnloadedEvent implements Event<AssetActivityListener> {
	AssetId assetId;
	Object asset;

	public void post(AssetId assetId, Object asset) {
		this.assetId = assetId;
		this.asset = asset;
		EventService.post(AssetActivityListener.class, this);
		this.assetId = null;
		this.asset = null;
	}

	@Override
	public void dispatch(AssetActivityListener listener) {
		listener.assetUnloaded(assetId, asset);
	}

	@Override
	public Class<AssetActivityListener> getSubscriptionType() {
		return AssetActivityListener.class;
	}
}
