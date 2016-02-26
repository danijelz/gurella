package com.gurella.engine.subscriptions.application;

import com.gurella.engine.event.EventSubscription;

public interface ApplicationActivityListener extends EventSubscription {
	void onPause();

	void onResume();
}
