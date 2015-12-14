package com.gurella.engine.event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class EventService {
	private static final EventBus globalEventBus = new EventBus();
	private static final ObjectMap<Object, EventBus> eventBuses = new ObjectMap<Object, EventBus>();

	public static <LISTENER> void addListener(Class<? extends Event<LISTENER>> eventType, LISTENER listener) {
		globalEventBus.addListener(eventType, listener);
	}

	public static <LISTENER> void removeListener(Class<? extends Event<LISTENER>> eventType, LISTENER listener) {
		globalEventBus.removeListener(eventType, listener);
	}

	public static <LISTENER> void notify(final Event<LISTENER> event) {
		globalEventBus.notify(event);
	}

	public static <T> void addListener(T eventType, Listener1<T> listener) {
		globalEventBus.addListener(eventType, listener);
	}

	public static <T> void removeListener(T eventType, Listener1<T> listener) {
		globalEventBus.removeListener(eventType, listener);
	}

	public static void notify(Object eventType) {
		globalEventBus.notify(eventType);
	}

	public <LISTENER> Array<? super LISTENER> getListeners(Class<? extends Event<LISTENER>> eventType,
			Array<? super LISTENER> out) {
		return globalEventBus.getListeners(eventType, out);
	}

	public <T> Array<Listener1<? super T>> getListeners(T eventType, Array<Listener1<? super T>> out) {
		return globalEventBus.getListeners(eventType, out);
	}

	public static <LISTENER> void addListener(Object source, Class<? extends Event<LISTENER>> eventType, LISTENER listener) {
		getEventBusBySource(source).addListener(eventType, listener);
	}

	private static EventBus getEventBusBySource(Object source) {
		synchronized (eventBuses) {
			EventBus eventBus = eventBuses.get(source);
			if (eventBus == null) {
				eventBus = new EventBus();
				eventBuses.put(source, eventBus);
			}
			return eventBus;
		}
	}

	public static <LISTENER> void removeListener(Object source, Class<? extends Event<LISTENER>> eventType, LISTENER listener) {
		EventBus eventBus;
		synchronized (eventBuses) {
			eventBus = eventBuses.get(source);
		}
		if (eventBus != null) {
			eventBus.removeListener(eventType, listener);
		}
	}

	public static <LISTENER> void notify(Object source, final Event<LISTENER> event) {
		EventBus eventBus;
		synchronized (eventBuses) {
			eventBus = eventBuses.get(source);
		}
		if (eventBus != null) {
			eventBus.notify(event);
		}
	}

	public static <T> void addListener(Object source, T eventType, Listener1<T> listener) {
		getEventBusBySource(source).addListener(eventType, listener);
	}

	public static <T> void removeListener(Object source, T eventType, Listener1<T> listener) {
		EventBus eventBus;
		synchronized (eventBuses) {
			eventBus = eventBuses.get(source);
		}
		if (eventBus != null) {
			eventBus.removeListener(eventType, listener);
		}
	}

	public static <T> void notify(Object source, T eventType) {
		EventBus eventBus;
		synchronized (eventBuses) {
			eventBus = eventBuses.get(source);
		}
		if (eventBus != null) {
			eventBus.notify(eventType);
		}
	}

	public <LISTENER> Array<? super LISTENER> getListeners(Object source, Class<? extends Event<LISTENER>> eventType,
			Array<? super LISTENER> out) {
		EventBus eventBus;
		synchronized (eventBuses) {
			eventBus = eventBuses.get(source);
		}
		if (eventBus != null) {
			eventBus.getListeners(eventType, out);
		}

		return out;
	}

	public <T> Array<Listener1<? super T>> getListeners(Object source, T eventType, Array<Listener1<? super T>> out) {
		EventBus eventBus;
		synchronized (eventBuses) {
			eventBus = eventBuses.get(source);
		}
		if (eventBus != null) {
			eventBus.getListeners(eventType, out);
		}

		return out;
	}
}
