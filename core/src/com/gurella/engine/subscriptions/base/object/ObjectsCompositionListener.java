package com.gurella.engine.subscriptions.base.object;

import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectsCompositionListener extends ApplicationEventSubscription {
	void childAdded(ManagedObject parent, ManagedObject child);

	void childRemoved(ManagedObject parent, ManagedObject child);
}
