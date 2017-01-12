package com.gurella.engine.subscriptions.managedobject;

import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectsParentListener extends ApplicationEventSubscription {
	void parentChanged(ManagedObject child, ManagedObject oldParent, ManagedObject newParent);
}
