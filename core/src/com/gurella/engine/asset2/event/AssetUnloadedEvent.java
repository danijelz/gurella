package com.gurella.engine.asset2.event;

import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.asset.AssetActivityListener2;

public class AssetUnloadedEvent implements Event<AssetActivityListener2> {
	AssetId assetId;
	Object asset;

	public void post(AssetId assetId, Object asset) {
		this.assetId = assetId;
		this.asset = asset;
		EventService.post(AssetActivityListener2.class, this);
		this.assetId = null;
		this.asset = null;
	}

	@Override
	public void dispatch(AssetActivityListener2 listener) {
		listener.assetUnloaded(assetId, asset);
	}

	@Override
	public Class<AssetActivityListener2> getSubscriptionType() {
		return AssetActivityListener2.class;
	}
}
