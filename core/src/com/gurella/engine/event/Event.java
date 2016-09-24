package com.gurella.engine.event;

public interface Event<LISTENER extends EventSubscription> {
	Class<LISTENER> getSubscriptionType();
}
