package com.gurella.engine.subscriptions.base.object;

import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventSubscription;

public interface ObjectsParentListener extends EventSubscription {
	void parentChanged(ManagedObject object, ManagedObject oldParent, ManagedObject newParent);
}
