package com.gurella.engine.event;

public interface Event<SUBSCRIBER extends EventSubscription> {
	Class<SUBSCRIBER> getSubscriptionType();
	
	void dispatch(SUBSCRIBER subscriber);
}
