package com.gurella.engine.event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.pool.PoolService;

public class EventService {
	private static final EventBus globalEventBus = new EventBus();
	private static final IntMap<EventBus> eventBuses = new IntMap<EventBus>();

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

	public static <LISTENER> void addListener(int channel, Class<? extends Event<LISTENER>> eventType,
			LISTENER listener) {
		getEventBusBySource(channel).addListener(eventType, listener);
	}

	private static EventBus getEventBusBySource(int channel) {
		synchronized (eventBuses) {
			EventBus eventBus = eventBuses.get(channel);
			if (eventBus == null) {
				eventBus = PoolService.obtain(EventBus.class);
				eventBuses.put(channel, eventBus);
			}
			return eventBus;
		}
	}

	public static <LISTENER> void removeListener(int channel, Class<? extends Event<LISTENER>> eventType,
			LISTENER listener) {
		synchronized (eventBuses) {
			EventBus eventBus = eventBuses.get(channel);
			if (eventBus != null) {
				eventBus.removeListener(eventType, listener);
				if(eventBus.isEmpty()) {
					PoolService.free(eventBus);
				}
			}
		}
	}

	public static <LISTENER> void notify(int channel, final Event<LISTENER> event) {
		synchronized (eventBuses) {
			EventBus eventBus = eventBuses.get(channel);
			if (eventBus != null) {
				eventBus.notify(event);
			}
		}
	}

	public static <T> void addListener(int channel, T eventType, Listener1<T> listener) {
		getEventBusBySource(channel).addListener(eventType, listener);
	}

	public static <T> void removeListener(int channel, T eventType, Listener1<T> listener) {
		synchronized (eventBuses) {
			EventBus eventBus = eventBuses.get(channel);
			if (eventBus != null) {
				eventBus.removeListener(eventType, listener);
				if(eventBus.isEmpty()) {
					PoolService.free(eventBus);
				}
			}
		}
	}

	public static <T> void notify(int channel, T eventType) {
		synchronized (eventBuses) {
			EventBus eventBus = eventBuses.get(channel);
			if (eventBus != null) {
				eventBus.notify(eventType);
			}
		}
	}

	public <LISTENER> Array<? super LISTENER> getListeners(int channel, Class<? extends Event<LISTENER>> eventType,
			Array<? super LISTENER> out) {
		synchronized (eventBuses) {
			EventBus eventBus = eventBuses.get(channel);
			if (eventBus != null) {
				eventBus.getListeners(eventType, out);
			}
		}
		return out;
	}

	public <T> Array<Listener1<? super T>> getListeners(int channel, T eventType, Array<Listener1<? super T>> out) {
		synchronized (eventBuses) {
			EventBus eventBus = eventBuses.get(channel);
			if (eventBus != null) {
				eventBus.getListeners(eventType, out);
			}
		}
		return out;
	}
}
