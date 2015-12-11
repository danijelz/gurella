package com.gurella.engine.event;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.Ordered;

public class EventBus2 {
	private final ObjectMap<Object, Array<?>> listeners = new ObjectMap<Object, Array<?>>();
	private final Array<Object> eventPool = new Array<Object>();

	private boolean processing;

	public <LISTENER> boolean addListener(Class<? extends Event<LISTENER>> eventClass, LISTENER listener) {
		final Array<LISTENER> listenersByType = listenersByType(eventClass);
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

	private <LISTENER> Array<LISTENER> listenersByType(Object eventType) {
		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			Array<LISTENER> listenersByType = (Array<LISTENER>) listeners.get(eventType);

			if (listenersByType == null) {
				listenersByType = new Array<LISTENER>();
				listeners.put(eventType, listenersByType);
			}

			return listenersByType;
		}
	}

	public <LISTENER> boolean removeListener(Class<? extends Event<LISTENER>> eventType, LISTENER listener) {
		Array<LISTENER> listenersByType;
		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			Array<LISTENER> casted = (Array<LISTENER>) listeners.get(eventType);
			if (casted == null) {
				return false;
			}

			listenersByType = casted;
		}

		synchronized (listenersByType) {
			return listenersByType.removeValue(listener, true);
		}
	}

	public <LISTENER> void notify(final Event<LISTENER> event) {
		synchronized (eventPool) {
			if (processing) {
				eventPool.add(event);
				return;
			} else {
				processing = true;
			}
		}

		notifyListeners(event);
	}

	private <LISTENER> void notifyListeners(final Event<LISTENER> event) {
		@SuppressWarnings("unchecked")
		Class<Event<LISTENER>> eventType = (Class<Event<LISTENER>>) event.getClass();
		Array<LISTENER> listenersByType;
		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			Array<LISTENER> casted = (Array<LISTENER>) listeners.get(eventType);
			if (casted == null) {
				return;
			}

			listenersByType = casted;
		}

		synchronized (listenersByType) {
			for (int i = 0; i < listenersByType.size; i++) {
				LISTENER listener = listenersByType.get(i);
				event.notify(listener);
			}
		}

		processPool();
	}

	private void processPool() {
		Object event = null;
		synchronized (eventPool) {
			if (eventPool.size > 0) {
				event = eventPool.removeIndex(0);
			} else {
				processing = false;
				return;
			}
		}

		if (event instanceof Event) {
			notifyListeners((Event<?>) event);
		} else {
			notifyListeners((String) event);
		}
	}

	public boolean addListener(String eventType, Listener1<String> listener) {
		final Array<Listener1<String>> listenersByType = listenersByType(eventType);
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

	public boolean removeListener(String eventType, Listener1<String> listener) {
		Array<Listener1<String>> listenersByType;
		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			Array<Listener1<String>> casted = (Array<Listener1<String>>) listeners.get(eventType);
			if (casted == null) {
				return false;
			}

			listenersByType = casted;
		}

		synchronized (listenersByType) {
			return listenersByType.removeValue(listener, true);
		}
	}

	public synchronized void notify(String eventType) {
		synchronized (eventPool) {
			if (processing) {
				eventPool.add(eventType);
			} else {
				processing = true;
			}
		}

		notifyListeners(eventType);
	}

	private void notifyListeners(String eventType) {
		Array<Listener1<String>> listenersByType;
		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			Array<Listener1<String>> casted = (Array<Listener1<String>>) listeners.get(eventType);
			if (casted == null) {
				return;
			}

			listenersByType = casted;
		}

		synchronized (listenersByType) {
			for (int i = 0; i < listenersByType.size; i++) {
				Listener1<String> listener = listenersByType.get(i);
				listener.handle(eventType);
			}
		}

		processPool();
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
