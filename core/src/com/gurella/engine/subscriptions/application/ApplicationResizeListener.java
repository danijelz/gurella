package com.gurella.engine.subscriptions.application;

public interface ApplicationResizeListener extends ApplicationEventSubscription {
	void resize(int width, int height);
}
