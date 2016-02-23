package com.gurella.engine.event;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.utils.Prioritized;
import com.gurella.engine.utils.ValueUtils;

public class EventBus implements Poolable {
	private final ObjectMap<Object, Array<?>> listeners = new ObjectMap<Object, Array<?>>();
	private final Array<Object> eventPool = new Array<Object>();

	private boolean processing;

	public <LISTENER> boolean addListener(Class<? extends Event<LISTENER>> eventType, LISTENER listener) {
		return addListenerInternal(eventType, listener);
	}

	public <T> boolean addListener(T eventType, Listener1<? super T> listener) {
		return addListenerInternal(eventType, listener);
	}

	private boolean addListenerInternal(Object eventType, Object listener) {
		final Array<Object> listenersByType = listenersByType(eventType);

		synchronized (listenersByType) {
			if (listenersByType.contains(listener, true)) {
				return false;
			} else {
				listenersByType.add(listener);
				if (listener instanceof Prioritized) {
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
		return removeListenerInternal(eventType, listener);
	}

	public <T> boolean removeListener(T eventType, Listener1<? super T> listener) {
		return removeListenerInternal(eventType, listener);
	}

	private boolean removeListenerInternal(Object eventType, Object listener) {
		Array<Object> listenersByType;
		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			Array<Object> casted = (Array<Object>) listeners.get(eventType);
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

	public void notify(Object eventType) {
		synchronized (eventPool) {
			if (processing) {
				eventPool.add(eventType);
			} else {
				processing = true;
			}
		}

		notifyListeners(eventType);
	}

	private <LISTENER> void notifyListeners(final Event<LISTENER> event) {
		Class<? extends Event<LISTENER>> eventType = ValueUtils.cast(event.getClass());
		Array<LISTENER> listenersByType;
		synchronized (listeners) {
			listenersByType = ValueUtils.cast(listeners.get(eventType));
			if (listenersByType == null) {
				return;
			}
		}

		synchronized (listenersByType) {
			for (int i = 0; i < listenersByType.size; i++) {
				LISTENER listener = listenersByType.get(i);
				event.notify(listener);
			}
		}

		processPool();
	}

	private <T> void notifyListeners(T eventType) {
		Array<Listener1<T>> listenersByType;
		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			Array<Listener1<T>> casted = (Array<Listener1<T>>) listeners.get(eventType);
			if (casted == null) {
				return;
			}

			listenersByType = casted;
		}

		synchronized (listenersByType) {
			for (int i = 0; i < listenersByType.size; i++) {
				Listener1<T> listener = listenersByType.get(i);
				listener.handle(eventType);
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
			notifyListeners(event);
		}
	}

	public <LISTENER> Array<? super LISTENER> getListeners(Class<? extends Event<LISTENER>> eventType,
			Array<? super LISTENER> out) {
		getListenersInternal(eventType, out);
		return out;
	}

	public <T> Array<Listener1<? super T>> getListeners(T eventType, Array<Listener1<? super T>> out) {
		getListenersInternal(eventType, out);
		return out;
	}

	public void getListenersInternal(Object eventType, Array<?> out) {
		@SuppressWarnings("unchecked")
		Array<Object> casted = (Array<Object>) out;
		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			Array<Object> listenersByType = (Array<Object>) listeners.get(eventType);
			if (listenersByType != null) {
				casted.addAll(listenersByType);
			}
		}
	}

	public boolean isEmpty() {
		synchronized (eventPool) {
			return eventPool.size == 0;
		}
	}

	@Override
	public void reset() {
		while (true) {
			synchronized (eventPool) {
				if (!processing) {
					resetInternal();
					return;
				}
			}
			ThreadUtils.yield();
		}
	}

	private void resetInternal() {
		listeners.clear();
		eventPool.clear();
		processing = false;
	}

	private static class ListenersComparator implements Comparator<Object> {
		private static ListenersComparator instance = new ListenersComparator();

		@Override
		public int compare(Object o1, Object o2) {
			return ValueUtils.compare(getPriority(o1), getPriority(o2));
		}

		private static int getPriority(Object o) {
			if (o instanceof Prioritized) {
				return ((Prioritized) o).getPriority();
			} else {
				return Integer.MAX_VALUE;
			}
		}
	}
}
