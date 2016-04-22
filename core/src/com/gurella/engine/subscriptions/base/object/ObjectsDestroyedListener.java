package com.gurella.engine.subscriptions.base.object;

import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectsDestroyedListener extends ApplicationEventSubscription {
	void objectDestroyed(ManagedObject object);
}
