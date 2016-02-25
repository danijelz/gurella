package com.gurella.engine.application.events;

import com.gurella.engine.event.Signal;

public class ApplicationResizeSignal extends Signal<ApplicationResizeSignal.ApplicationResizeListener> {
	public void onResize(int width, int height) {
		ApplicationResizeListener[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			items[i].onResize(width, height);
		}
		listeners.end();
	}

	public interface ApplicationResizeListener {
		void onResize(int width, int height);
	}
}
