package com.gurella.engine.event;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.signal.Listener1;
import com.gurella.engine.utils.Ordered;

public class EventBus {
	public static final EventBus GLOBAL = new EventBus();
	
	private final ObjectMap<Class<? extends Event<?>>, Array<?>> listeners = new ObjectMap<Class<? extends Event<?>>, Array<?>>();
	private final ObjectMap<String, Array<Listener1<String>>> simpleListeners = new ObjectMap<String, Array<Listener1<String>>>();
	private final Array<Object> eventPool = new Array<Object>();
	
	private static boolean processing;

	public <LISTENER> boolean addListener(Class<? extends Event<LISTENER>> eventClass, LISTENER listener) {
		final Array<LISTENER> listenersByType = listenersByType(eventClass);
		synchronized (listenersByType) {
			if (listenersByType.contains(listener, true)) {
				return false;
			} else {
				listenersByType.add(listener);
				//TODO Ordered.getOrder() should be replaced with something more meaningful
				if (listener instanceof Ordered) {
					listenersByType.sort(ListenersComparator.instance);
				}
				return true;
			}
		}
	}

	private <LISTENER> Array<LISTENER> listenersByType(Class<? extends Event<LISTENER>> eventClass) {
		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			Array<LISTENER> listenersByType = (Array<LISTENER>) listeners.get(eventClass);

			if (listenersByType == null) {
				listenersByType = new Array<LISTENER>();
				listeners.put(eventClass, listenersByType);
			}

			return listenersByType;
		}
	}

	public <LISTENER> boolean removeListener(Class<? extends Event<LISTENER>> eventClass, LISTENER listener) {
		final Array<LISTENER> listenersByType = listenersByType(eventClass);
		synchronized (listenersByType) {
			return listenersByType.removeValue(listener, true);
		}
	}

	public synchronized <LISTENER> void notify(final Event<LISTENER> event) {
		if (processing) {
			eventPool.add(event);
		} else {
			processing = true;
			notifyListeners(event);
			processing = false;
		}
	}

	private <LISTENER> void notifyListeners(final Event<LISTENER> event) {
		@SuppressWarnings("unchecked")
		Class<Event<LISTENER>> eventClass = (Class<Event<LISTENER>>) event.getClass();

		for (LISTENER listener : listenersByType(eventClass)) {
			event.notify(listener);
		}

		processPooledEvents();
	}

	private void processPooledEvents() {
		if (eventPool.size > 0) {
			Object pooledEvent = eventPool.removeIndex(0);
			if(pooledEvent instanceof Event) {
				notifyListeners((Event<?>) pooledEvent);
			} else {
				notifyListeners((String) pooledEvent);
			}
		}
	}
	
	public boolean addListener(String evenType, Listener1<String> listener) {
		final Array<Listener1<String>> listenersByType = simpleListenersByType(evenType);
		synchronized (listenersByType) {
			if (listenersByType.contains(listener, true)) {
				return false;
			} else {
				listenersByType.add(listener);
				//TODO Ordered.getOrder() should be replaced with something more meaningful
				if (listener instanceof Ordered) {
					listenersByType.sort(ListenersComparator.instance);
				}
				return true;
			}
		}
	}
	
	private Array<Listener1<String>> simpleListenersByType(String evenType) {
		synchronized (simpleListeners) {
			Array<Listener1<String>> listenersByType = simpleListeners.get(evenType);

			if (listenersByType == null) {
				listenersByType = new Array<Listener1<String>>();
				simpleListeners.put(evenType, listenersByType);
			}

			return listenersByType;
		}
	}
	
	public boolean removeListener(String evenType, Listener1<String> listener) {
		final Array<Listener1<String>> listenersByType = simpleListenersByType(evenType);
		synchronized (listenersByType) {
			return listenersByType.removeValue(listener, true);
		}
	}

	public synchronized void notify(String evenType) {
		if (processing) {
			eventPool.add(evenType);
		} else {
			processing = true;
			notifyListeners(evenType);
			processing = false;
		}
	}

	private void notifyListeners(String evenType) {
		final Array<Listener1<String>> listenersByType = simpleListenersByType(evenType);

		for (int i = 0; i < listenersByType.size; i++) {
			Listener1<String> listener = listenersByType.get(0);
			listener.handle(evenType);
		}

		processPooledEvents();
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
