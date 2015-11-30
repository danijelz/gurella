package com.gurella.engine.event;

import com.gurella.engine.signal.Listener1;

public class EventService {
	private static final EventBus globalEventBus = new EventBus();

	public static <LISTENER> boolean addListener(Class<? extends Event<LISTENER>> eventClass, LISTENER listener) {
		return globalEventBus.addListener(eventClass, listener);
	}

	public static <LISTENER> boolean removeListener(Class<? extends Event<LISTENER>> eventClass, LISTENER listener) {
		return globalEventBus.removeListener(eventClass, listener);
	}

	public static <LISTENER> void notify(final Event<LISTENER> event) {
		globalEventBus.notify(event);
	}

	public static boolean addListener(String evenType, Listener1<String> listener) {
		return globalEventBus.addListener(evenType, listener);
	}

	public static boolean removeListener(String evenType, Listener1<String> listener) {
		return globalEventBus.removeListener(evenType, listener);
	}

	public static void notify(String evenType) {
		globalEventBus.notify(evenType);
	}
}
