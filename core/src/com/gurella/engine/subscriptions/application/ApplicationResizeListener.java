package com.gurella.engine.subscriptions.application;

public interface ApplicationResizeListener extends ApplicationEventSubscription {
	void onResize(int width, int height);
}
