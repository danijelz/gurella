package com.gurella.engine.application.events;

import com.gurella.engine.event.Signal;
import com.gurella.engine.utils.Prioritized;

//TODO update order
public class ApplicationUpdateSignal extends Signal<ApplicationUpdateSignal.ApplicationUpdateListener> {
	public void update() {
		ApplicationUpdateListener[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			items[i].update();
		}
		listeners.end();
	}

	public interface ApplicationUpdateListener extends Prioritized {
		void update();
	}
}
