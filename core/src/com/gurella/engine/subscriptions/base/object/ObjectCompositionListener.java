package com.gurella.engine.subscriptions.base.object;

import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventSubscription;

public interface ObjectCompositionListener extends EventSubscription {
	void childAdded(ManagedObject child);

	void childRemoved(ManagedObject child);
}
