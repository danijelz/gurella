package com.gurella.engine.application.events;

import com.gurella.engine.event.AbstractSignal;

//TODO update order
public class ApplicationUpdateSignal extends AbstractSignal<UpdateListener> {
	public void update() {
		UpdateListener[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			items[i].update();
		}
		listeners.end();
	}
}
