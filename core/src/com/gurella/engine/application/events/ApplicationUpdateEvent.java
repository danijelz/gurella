package com.gurella.engine.application.events;

import com.gurella.engine.application.events.ApplicationUpdateSignal.ApplicationUpdateListener;
import com.gurella.engine.event.Event;

public class ApplicationUpdateEvent implements Event<ApplicationUpdateListener> {
	public static ApplicationUpdateEvent instance = new ApplicationUpdateEvent();

	private ApplicationUpdateEvent() {
	}

	@Override
	public void notify(ApplicationUpdateListener listener) {
		listener.update();
	}
}
