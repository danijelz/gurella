package com.gurella.engine.event;

public interface Event0<LISTENER extends EventSubscription> extends Event<LISTENER> {
	void notify(LISTENER listener);
}
