package com.gurella.engine.event;

public class Signal1<EVENT> extends Signal<Listener1<EVENT>> {
	public void dispatch(EVENT event) {
		Object[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			((Listener1<EVENT>)items[i]).handle(event);//TODO
		}
		listeners.end();
	}
}
