package com.gurella.engine.event;

public abstract class SubscriptionEvent<LISTENER extends EventSubscription> {
	public final Class<LISTENER> subscriptionType;

	public SubscriptionEvent(Class<LISTENER> subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	protected abstract void notify(LISTENER listener);
}
