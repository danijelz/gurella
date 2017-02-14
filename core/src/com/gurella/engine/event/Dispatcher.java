package com.gurella.engine.event;

public interface Dispatcher<SUBSCRIBER extends EventSubscription> {
	void dispatch(SUBSCRIBER subscriber);
}