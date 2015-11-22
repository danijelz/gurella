package com.gurella.engine.event;

public interface Event<LISTENER> {
	void notify(LISTENER listener);
}
