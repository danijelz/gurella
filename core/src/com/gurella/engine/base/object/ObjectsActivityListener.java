package com.gurella.engine.base.object;

import com.gurella.engine.scene.event.EventSubscription;

public interface ObjectsActivityListener extends EventSubscription {
	void objectActivated(ManagedObject object);

	void objectDeactivated(ManagedObject object);
}
