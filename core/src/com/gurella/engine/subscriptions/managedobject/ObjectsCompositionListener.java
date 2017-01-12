package com.gurella.engine.subscriptions.managedobject;

import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectsCompositionListener extends ApplicationEventSubscription {
	void childAdded(ManagedObject parent, ManagedObject child);

	void childRemoved(ManagedObject parent, ManagedObject child);
}
