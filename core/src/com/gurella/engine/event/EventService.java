package com.gurella.engine.event;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.signal.Listener1;
import com.gurella.engine.utils.Ordered;

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

	public boolean addListener(String evenType, Listener1<String> listener) {
		return globalEventBus.addListener(evenType, listener);
	}

	public boolean removeListener(String evenType, Listener1<String> listener) {
		final Array<Listener1<String>> listenersByType = simpleListenersByType(evenType);
		synchronized (listenersByType) {
			return listenersByType.removeValue(listener, true);
		}
	}

	public static void notify(String evenType) {
		if (processing) {
			eventPool.add(evenType);
		} else {
			processing = true;
			notifyListeners(evenType);
			processing = false;
		}
	}
}
