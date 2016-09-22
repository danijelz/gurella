package com.gurella.engine.event;

public interface Event<LISTENER extends EventSubscription, DATA> {
	Class<LISTENER> getSubscriptionType();

	void notify(LISTENER listener, DATA data);
}
