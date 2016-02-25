package com.gurella.engine.event;

public class Signal2<ARG1, ARG2> extends Signal<Listener2<ARG1, ARG2>> {
	public void dispatch(ARG1 arg1, ARG2 arg2) {
		Listener2<ARG1, ARG2>[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			items[i].handle(arg1, arg2);
		}
		listeners.end();
	}
}
