package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.event.EventSubscription;

public interface PreRenderUpdateListener extends EventSubscription {
	void onPreRenderUpdate();
}
