package com.gurella.engine.event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.pool.PoolService;

public class EventService {
	private static final EventBus globalEventBus = new EventBus();
	private static final IntMap<EventBus> eventBuses = new IntMap<EventBus>();

	public static <L> void addListener(Class<? extends Event<L>> eventType, L listener) {
		globalEventBus.addListener(eventType, listener);
	}

	public static <L> void removeListener(Class<? extends Event<L>> eventType, L listener) {
		globalEventBus.removeListener(eventType, listener);
	}

	public static <L> void notify(final Event<L> event) {
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

	public <L> Array<? super L> getListeners(Class<? extends Event<L>> eventType, Array<? super L> out) {
		return globalEventBus.getListeners(eventType, out);
	}

	public <T> Array<Listener1<? super T>> getListeners(T eventType, Array<Listener1<? super T>> out) {
		return globalEventBus.getListeners(eventType, out);
	}

	public static <L> void addListener(int channel, Class<? extends Event<L>> eventType, L listener) {
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

	public static <L> void removeListener(int channel, Class<? extends Event<L>> eventType, L listener) {
		synchronized (eventBuses) {
			EventBus eventBus = eventBuses.get(channel);
			if (eventBus != null) {
				eventBus.removeListener(eventType, listener);
				if (eventBus.isEmpty()) {
					PoolService.free(eventBus);
				}
			}
		}
	}

	public static <L> void notify(int channel, final Event<L> event) {
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
				if (eventBus.isEmpty()) {
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

	public <L> Array<? super L> getListeners(int channel, Class<? extends Event<L>> eventType, Array<? super L> out) {
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
