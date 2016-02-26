package com.gurella.engine.base.object;

import com.gurella.engine.event.EventSubscription;

//TODO unused
public interface ObjectsActivityListener extends EventSubscription {
	void objectActivated(ManagedObject object);

	void objectDeactivated(ManagedObject object);
}
