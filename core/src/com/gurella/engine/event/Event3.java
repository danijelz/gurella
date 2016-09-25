package com.gurella.engine.event;

public interface Event3<LISTENER extends EventSubscription, DATA1, DATA2, DATA3> extends Event<LISTENER> {
	void notify(LISTENER listener, DATA1 data, DATA2 data2, DATA3 data3);
}