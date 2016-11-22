package com.gurella.engine.asset;

import com.gurella.engine.event.Event;
import com.gurella.engine.subscriptions.base.resource.AssetActivityListener;

class AssetReloadedEvent implements Event<AssetActivityListener> {
	String fileName;
	Object asset;

	void set(String fileName, Object asset) {
		this.fileName = fileName;
		this.asset = asset;
	}

	@Override
	public void dispatch(AssetActivityListener listener) {
		listener.assetReloaded(fileName, asset);
	}

	@Override
	public Class<AssetActivityListener> getSubscriptionType() {
		return AssetActivityListener.class;
	}

	void reset() {
		fileName = null;
		asset = null;
	}
}
