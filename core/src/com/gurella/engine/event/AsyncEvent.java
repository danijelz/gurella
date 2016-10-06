package com.gurella.engine.event;

//TODO unused
public interface AsyncEvent<SUBSCRIBER extends EventSubscription> extends Event<SUBSCRIBER> {
	void onSuccess();

	void onException(Throwable exception);
}
