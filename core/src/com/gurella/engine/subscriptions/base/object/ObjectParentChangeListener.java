package com.gurella.engine.subscriptions.base.object;

import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectParentChangeListener extends ApplicationEventSubscription {
	void parentChanged(ManagedObject oldParent, ManagedObject newParent);
}
