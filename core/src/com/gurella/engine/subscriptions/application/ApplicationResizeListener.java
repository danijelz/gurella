package com.gurella.engine.subscriptions.application;

import com.gurella.engine.event.EventSubscription;

public interface ApplicationResizeListener extends EventSubscription {
	void onResize(int width, int height);
}
