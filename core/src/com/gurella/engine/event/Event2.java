package com.gurella.engine.event;

public interface Event2<LISTENER extends EventSubscription, DATA1, DATA2> {
	Class<LISTENER> getSubscriptionType();

	void notify(LISTENER listener, DATA1 data, DATA2 data2);
}
