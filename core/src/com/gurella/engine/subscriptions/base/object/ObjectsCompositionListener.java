package com.gurella.engine.subscriptions.base.object;

import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventSubscription;

public interface ObjectsCompositionListener extends EventSubscription {
	void childAdded(ManagedObject object, ManagedObject child);

	void childRemoved(ManagedObject object, ManagedObject child);
}
