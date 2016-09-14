package com.gurella.engine.event;

public class Signal2<ARG1, ARG2> extends Signal<Listener2<ARG1, ARG2>> {
	public void dispatch(ARG1 arg1, ARG2 arg2) {
		Object[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			@SuppressWarnings("unchecked")
			Listener2<ARG1, ARG2> listener = (Listener2<ARG1, ARG2>) items[i];
			listener.handle(arg1, arg2);
		}
		listeners.end();
	}
}
