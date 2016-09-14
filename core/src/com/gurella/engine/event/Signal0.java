package com.gurella.engine.event;

public class Signal0 extends Signal<Listener0> {
	public void dispatch() {
		Object[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			Listener0 listener = (Listener0) items[i];
			listener.handle();
		}
		listeners.end();
	}
}
