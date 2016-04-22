package com.gurella.engine.subscriptions.base.object;

import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectDestroyedListener extends ApplicationEventSubscription {
	void objectDestroyed();
}
