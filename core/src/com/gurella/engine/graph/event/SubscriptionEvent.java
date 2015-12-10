package com.gurella.engine.graph.event;

import com.gurella.engine.event.Event;

public abstract class SubscriptionEvent<LISTENER extends EventSubscription> implements Event<LISTENER> {
	public final Class<LISTENER> subscriptionType;

	public SubscriptionEvent(Class<LISTENER> subscriptionType) {
		this.subscriptionType = subscriptionType;
	}
}
