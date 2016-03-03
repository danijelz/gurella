package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.event.EventSubscription;

public interface IoUpdateListener extends EventSubscription {
	void onIoUpdate();
}
