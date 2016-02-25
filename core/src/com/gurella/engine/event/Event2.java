package com.gurella.engine.event;

public class Event2<ARG1, ARG2> implements Event<Listener2<ARG1, ARG2>> {
	protected ARG1 arg1;
	protected ARG2 arg2;

	@Override
	public void notify(Listener2<ARG1, ARG2> listener) {
		listener.handle(arg1, arg2);
	}
}
