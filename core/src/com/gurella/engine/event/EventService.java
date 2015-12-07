package com.gurella.engine.event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.signal.Listener1;
import com.gurella.engine.utils.ImmutableArray;

public class EventService {
	private static final EventBus globalEventBus = new EventBus();
	private static final ObjectMap<Object, EventBus> eventBuses = new ObjectMap<Object, EventBus>();

	public static <LISTENER> boolean addListener(Class<? extends Event<LISTENER>> eventClass, LISTENER listener) {
		return globalEventBus.addListener(eventClass, listener);
	}

	public static <LISTENER> boolean removeListener(Class<? extends Event<LISTENER>> eventClass, LISTENER listener) {
		return globalEventBus.removeListener(eventClass, listener);
	}

	public static <LISTENER> void notify(final Event<LISTENER> event) {
		globalEventBus.notify(event);
	}

	public static boolean addListener(Object evenType, Listener1<Object> listener) {
		return globalEventBus.addListener(evenType, listener);
	}

	public static boolean removeListener(Object evenType, Listener1<Object> listener) {
		return globalEventBus.removeListener(evenType, listener);
	}

	public static void notify(Object evenType) {
		globalEventBus.notify(evenType);
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

	public static <LISTENER> boolean addListener(Object source, Class<? extends Event<LISTENER>> eventClass,
			LISTENER listener) {
		return getEventBusBySource(source).addListener(eventClass, listener);
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

	public static <LISTENER> boolean removeListener(Object source, Class<? extends Event<LISTENER>> eventClass,
			LISTENER listener) {
		return getEventBusBySource(source).removeListener(eventClass, listener);
	}

	public static <LISTENER> void notify(Object source, final Event<LISTENER> event) {
		getEventBusBySource(source).notify(event);
	}

	public static <T> boolean addListener(Object source, T evenType, Listener1<T> listener) {
		return getEventBusBySource(source).addListener(evenType, listener);
	}

	public static <T> boolean removeListener(Object source, T evenType, Listener1<T> listener) {
		return getEventBusBySource(source).removeListener(evenType, listener);
	}

	public static <T> void notify(Object source, T evenType) {
		getEventBusBySource(source).notify(evenType);
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
