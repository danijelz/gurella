package com.gurella.engine.subscriptions.managedobject;

import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectParentChangeListener extends ApplicationEventSubscription {
	void onParentChanged(ManagedObject oldParent, ManagedObject newParent);
}
