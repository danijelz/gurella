package com.gurella.engine.subscriptions.managedobject;

import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

public interface ObjectCompositionListener extends ApplicationEventSubscription {
	void onChildAdded(ManagedObject child);

	void onChildRemoved(ManagedObject child);
}
