package com.gurella.engine.asset2.registry;

import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.asset.AssetActivityListener;

class AssetUnloadedEvent implements Event<AssetActivityListener> {
	String fileName;
	Object asset;

	void post(String fileName, Object asset) {
		this.fileName = fileName;
		this.asset = asset;
		EventService.post(AssetActivityListener.class, this);
		reset();
	}

	@Override
	public void dispatch(AssetActivityListener listener) {
		listener.assetUnloaded(fileName, asset);
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
