package com.gurella.engine.subscriptions.base.object;

import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectActivityListener extends ApplicationEventSubscription {
	void activated();

	void deactivated();
}
