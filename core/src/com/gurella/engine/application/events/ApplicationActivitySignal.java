package com.gurella.engine.application.events;

import com.gurella.engine.event.AbstractSignal;

public class ApplicationActivitySignal extends AbstractSignal<ApplicationActivitySignal.ApplicationActivityListener> {
	public void onPause() {
		ApplicationActivityListener[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			items[i].onPause();
		}
		listeners.end();
	}

	public void onResume() {
		ApplicationActivityListener[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			items[i].onResume();
		}
		listeners.end();
	}

	public interface ApplicationActivityListener {
		void onPause();

		void onResume();
	}
}
