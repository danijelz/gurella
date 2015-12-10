package com.gurella.engine.event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.ImmutableArray;

public class EventService {
	private static final EventBus globalEventBus = new EventBus();
	private static final ObjectMap<Object, EventBus> eventBuses = new ObjectMap<Object, EventBus>();

	public static <LISTENER> void addListener(Class<? extends Event<LISTENER>> eventClass, LISTENER listener) {
		globalEventBus.addListener(eventClass, listener);
	}

	public static <LISTENER> void removeListener(Class<? extends Event<LISTENER>> eventClass, LISTENER listener) {
		globalEventBus.removeListener(eventClass, listener);
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

	public <LISTENER> ImmutableArray<LISTENER> getListeners(Class<? extends Event<LISTENER>> eventType) {
		return globalEventBus.getListeners(eventType);
	}

	public <LISTENER> Array<LISTENER> getListeners(Class<? extends Event<LISTENER>> eventType, Array<LISTENER> out) {
		return globalEventBus.getListeners(eventType, out);
	}

	public <T> ImmutableArray<Listener1<? super T>> getListeners(T eventType) {
		return globalEventBus.getListeners(eventType);
	}

	public <T> Array<Listener1<? super T>> getListeners(T eventType, Array<Listener1<? super T>> out) {
		return globalEventBus.getListeners(eventType, out);
	}

	public static <LISTENER> void addListener(Object source, Class<? extends Event<LISTENER>> eventClass,
			LISTENER listener) {
		getEventBusBySource(source).addListener(eventClass, listener);
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

	public static <LISTENER> void removeListener(Object source, Class<? extends Event<LISTENER>> eventClass,
			LISTENER listener) {
		getEventBusBySource(source).removeListener(eventClass, listener);
	}

	public static <LISTENER> void notify(Object source, final Event<LISTENER> event) {
		getEventBusBySource(source).notify(event);
	}

	public static <T> void addListener(Object source, T eventType, Listener1<T> listener) {
		getEventBusBySource(source).addListener(eventType, listener);
	}

	public static <T> void removeListener(Object source, T eventType, Listener1<T> listener) {
		getEventBusBySource(source).removeListener(eventType, listener);
	}

	public static <T> void notify(Object source, T eventType) {
		getEventBusBySource(source).notify(eventType);
	}

	public <LISTENER> ImmutableArray<LISTENER> getListeners(Object source, Class<? extends Event<LISTENER>> eventType) {
		return getEventBusBySource(source).getListeners(eventType);
	}

	public <LISTENER> Array<LISTENER> getListeners(Object source, Class<? extends Event<LISTENER>> eventType,
			Array<LISTENER> out) {
		return getEventBusBySource(source).getListeners(eventType, out);
	}

	public <T> ImmutableArray<Listener1<? super T>> getListeners(Object source, T eventType) {
		return getEventBusBySource(source).getListeners(eventType);
	}

	public <T> Array<Listener1<? super T>> getListeners(Object source, T eventType, Array<Listener1<? super T>> out) {
		return getEventBusBySource(source).getListeners(eventType, out);
	}
}
