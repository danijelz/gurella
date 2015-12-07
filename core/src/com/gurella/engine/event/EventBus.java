package com.gurella.engine.event;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.signal.Listener1;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Ordered;

public class EventBus {
	private final ObjectMap<Class<? extends Event<?>>, ArrayExt<?>> listeners = new ObjectMap<Class<? extends Event<?>>, ArrayExt<?>>();
	private final ObjectMap<Object, ArrayExt<?>> simpleListeners = new ObjectMap<Object, ArrayExt<?>>();
	private final Array<Object> eventPool = new Array<Object>();

	private boolean processing;

	public <LISTENER> boolean addListener(Class<? extends Event<LISTENER>> eventType, LISTENER listener) {
		final ArrayExt<LISTENER> listenersByType = listenersByType(eventType);
		synchronized (listenersByType) {
			if (listenersByType.contains(listener, true)) {
				return false;
			} else {
				listenersByType.add(listener);
				if (listener instanceof Ordered) {
					listenersByType.sort(ListenersComparator.instance);
				}
				return true;
			}
		}
	}

	private <LISTENER> ArrayExt<LISTENER> listenersByType(Class<? extends Event<LISTENER>> eventType) {
		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			ArrayExt<LISTENER> listenersByType = (ArrayExt<LISTENER>) listeners.get(eventType);

			if (listenersByType == null) {
				listenersByType = new ArrayExt<LISTENER>();
				listeners.put(eventType, listenersByType);
			}

			return listenersByType;
		}
	}

	public <LISTENER> boolean removeListener(Class<? extends Event<LISTENER>> eventType, LISTENER listener) {
		final ArrayExt<LISTENER> listenersByType = listenersByType(eventType);
		synchronized (listenersByType) {
			return listenersByType.removeValue(listener, true);
		}
	}

	public <LISTENER> void notify(final Event<LISTENER> event) {
		if (checkProcessEvents(event)) {
			notifyListeners(event);
		}
	}

	private boolean checkProcessEvents(Object event) {
		synchronized (eventPool) {
			if (processing) {
				eventPool.add(event);
				return false;
			} else {
				processing = true;
				return true;
			}
		}
	}

	private <LISTENER> void notifyListeners(final Event<LISTENER> event) {
		@SuppressWarnings("unchecked")
		Class<Event<LISTENER>> eventClass = (Class<Event<LISTENER>>) event.getClass();
		final ArrayExt<LISTENER> listenersByType = listenersByType(eventClass);

		synchronized (listenersByType) {
			for (int i = 0; i < listenersByType.size; i++) {
				LISTENER listener = listenersByType.get(i);
				event.notify(listener);
			}
		}

		processPooledEvents();
	}

	private void processPooledEvents() {
		Object pooledEvent = null;
		synchronized (eventPool) {
			if (eventPool.size > 0) {
				pooledEvent = eventPool.removeIndex(0);
			} else {
				processing = false;
			}
		}

		if (pooledEvent != null) {
			if (pooledEvent instanceof Event) {
				notifyListeners((Event<?>) pooledEvent);
			} else {
				notifyListeners(pooledEvent);
			}
		}
	}

	public <LISTENER> ImmutableArray<LISTENER> getListeners(Class<? extends Event<LISTENER>> eventType) {
		return listenersByType(eventType).immutable();
	}

	public <LISTENER> Array<LISTENER> getListeners(Class<? extends Event<LISTENER>> eventType, Array<LISTENER> out) {
		out.addAll(listenersByType(eventType));
		return out;
	}

	public <T> boolean addListener(T eventType, Listener1<? super T> listener) {
		final ArrayExt<Listener1<? super T>> listenersByType = simpleListenersByType(eventType);
		synchronized (listenersByType) {
			if (listenersByType.contains(listener, true)) {
				return false;
			} else {
				listenersByType.add(listener);
				if (listener instanceof Ordered) {
					listenersByType.sort(ListenersComparator.instance);
				}
				return true;
			}
		}
	}

	private <T> ArrayExt<Listener1<? super T>> simpleListenersByType(T eventType) {
		synchronized (simpleListeners) {
			@SuppressWarnings("unchecked")
			ArrayExt<Listener1<? super T>> listenersByType = (ArrayExt<Listener1<? super T>>) simpleListeners
					.get(eventType);

			if (listenersByType == null) {
				listenersByType = new ArrayExt<Listener1<? super T>>();
				simpleListeners.put(eventType, listenersByType);
			}

			return listenersByType;
		}
	}

	public <T> boolean removeListener(T event, Listener1<? super T> listener) {
		final ArrayExt<Listener1<? super T>> listenersByType = simpleListenersByType(event);
		synchronized (listenersByType) {
			return listenersByType.removeValue(listener, true);
		}
	}

	public void notify(Object event) {
		if (checkProcessEvents(event)) {
			notifyListeners(event);
		}
	}

	private <T> void notifyListeners(T evenType) {
		final ArrayExt<Listener1<? super T>> listenersByType = simpleListenersByType(evenType);

		synchronized (listenersByType) {
			for (int i = 0; i < listenersByType.size; i++) {
				Listener1<? super T> listener = listenersByType.get(i);
				listener.handle(evenType);
			}
		}

		processPooledEvents();
	}

	public <T> ImmutableArray<Listener1<? super T>> getListeners(T eventType) {
		return simpleListenersByType(eventType).immutable();
	}

	public <T> Array<Listener1<? super T>> getListeners(T eventType, Array<Listener1<? super T>> out) {
		out.addAll(simpleListenersByType(eventType));
		return out;
	}

	private static class ListenersComparator implements Comparator<Object> {
		private static ListenersComparator instance = new ListenersComparator();

		@Override
		public int compare(Object o1, Object o2) {
			return Integer.compare(getPriority(o1), getPriority(o2));
		}

		private static int getPriority(Object o) {
			if (o instanceof Ordered) {
				return ((Ordered) o).getOrdinal();
			} else {
				return Integer.MAX_VALUE;
			}
		}
	}
}
