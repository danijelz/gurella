package com.gurella.engine.subscriptions.application;

public interface ApplicationActivityListener extends ApplicationEventSubscription {
	void onPause();

	void onResume();
}
