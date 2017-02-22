package com.gurella.engine.subscriptions.managedobject;

import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectDestroyedListener extends ApplicationEventSubscription {
	void onObjectDestroyed();
}
