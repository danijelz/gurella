package com.gurella.engine.application.events;

import com.gurella.engine.event.Signal;

public class ApplicationShutdownSignal extends Signal<ApplicationShutdownSignal.ApplicationShutdownListener> {
	public void onShutdown() {
		ApplicationShutdownListener[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			items[i].onShutdown();
		}
		listeners.end();
	}

	public interface ApplicationShutdownListener {
		void onShutdown();
	}
}
