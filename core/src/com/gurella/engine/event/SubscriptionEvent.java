package com.gurella.engine.event;

//TODO unused
public abstract class SubscriptionEvent<LISTENER extends EventSubscription> implements Event<LISTENER> {
	public final Class<LISTENER> subscriptionType;

	public SubscriptionEvent(Class<LISTENER> subscriptionType) {
		this.subscriptionType = subscriptionType;
	}
}
