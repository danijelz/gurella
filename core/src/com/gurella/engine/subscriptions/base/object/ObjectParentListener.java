package com.gurella.engine.subscriptions.base.object;

import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventSubscription;

public interface ObjectParentListener extends EventSubscription {
	void parentChanged(ManagedObject oldParent, ManagedObject newParent);
}
