package com.gurella.engine.subscriptions.managedobject;

import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectActivityListener extends ApplicationEventSubscription {
	void activated();

	void deactivated();
}
