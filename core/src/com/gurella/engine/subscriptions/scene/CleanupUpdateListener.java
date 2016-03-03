package com.gurella.engine.subscriptions.scene;

import com.gurella.engine.event.EventSubscription;

public interface CleanupUpdateListener extends EventSubscription {
	void onCleanupUpdate();
}
