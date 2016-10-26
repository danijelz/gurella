package com.gurella.engine.event;

public interface Event<SUBSCRIBER extends EventSubscription> extends Dispatcher<SUBSCRIBER> {
	Class<SUBSCRIBER> getSubscriptionType();
}
