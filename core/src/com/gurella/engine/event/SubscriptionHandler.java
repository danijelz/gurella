package com.gurella.engine.event;

//TODO unused
public abstract class SubscriptionHandler<LISTENER extends EventSubscription> {
	public final Class<LISTENER> subscriptionType;

	public SubscriptionHandler(Class<LISTENER> subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	protected abstract void notify(LISTENER listener);
}
