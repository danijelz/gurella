package com.gurella.engine.subscriptions.application;

public interface ApplicationActivityListener extends ApplicationEventSubscription {
	void pause();

	void resume();
}
