package com.gurella.engine.base.object;

import com.gurella.engine.event.EventSubscription;

public interface ObjectActivityListener extends EventSubscription {
	void activated();

	void deactivated();
}
