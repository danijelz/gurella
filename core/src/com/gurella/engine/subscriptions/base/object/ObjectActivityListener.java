package com.gurella.engine.subscriptions.base.object;

import com.gurella.engine.event.EventSubscription;

//TODO unused
public interface ObjectActivityListener extends EventSubscription {
	void activated();

	void deactivated();
}
