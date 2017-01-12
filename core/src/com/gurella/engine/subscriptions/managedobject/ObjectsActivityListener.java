package com.gurella.engine.subscriptions.managedobject;

import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectsActivityListener extends ApplicationEventSubscription {
	void objectActivated(ManagedObject object);

	void objectDeactivated(ManagedObject object);
}
