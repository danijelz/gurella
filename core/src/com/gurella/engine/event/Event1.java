package com.gurella.engine.event;

public interface Event1<LISTENER extends EventSubscription, DATA> extends Event<LISTENER> {
	void notify(LISTENER listener, DATA data);
}
