package com.gurella.engine.scene.event;

import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventSubscription;

public abstract class SubscriptionEvent<LISTENER extends EventSubscription> implements Event<LISTENER> {
	public final Class<LISTENER> subscriptionType;

	public SubscriptionEvent(Class<LISTENER> subscriptionType) {
		this.subscriptionType = subscriptionType;
	}
}
