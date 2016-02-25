package com.gurella.engine.base.object;

import com.gurella.engine.scene.event.EventSubscription;

public interface ObjectActivityListener extends EventSubscription {
	void activated();

	void deactivated();
}
