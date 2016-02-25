package com.gurella.engine.event;

public class Signal0 extends Signal<Listener0> {
	public void dispatch() {
		Listener0[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			items[i].handle();
		}
		listeners.end();
	}
}
