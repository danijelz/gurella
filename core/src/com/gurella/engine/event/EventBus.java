package com.gurella.engine.event;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.gurella.engine.signal.Listener1;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Ordered;

public class EventBus {
	private static final Object clear = new Object();

	private final ObjectMap<Object, ArrayExt<?>> listeners = new ObjectMap<Object, ArrayExt<?>>();
	private final Array<Object> pool = new Array<Object>();

	private boolean processing;

	public <LISTENER> void addListener(Class<? extends Event<LISTENER>> eventType, LISTENER listener) {
		add(eventType, listener);
	}

	public <T> void addListener(T eventType, Listener1<? super T> listener) {
		add(eventType, listener);
	}

	private void add(Object eventType, Object listener) {
		synchronized (pool) {
			if (processing) {
				EventBusAction eventBusAction = Pools.obtain(EventBusAction.class);
				eventBusAction.add = true;
				eventBusAction.eventType = eventType;
				eventBusAction.listener = listener;
				pool.add(eventBusAction);
				return;
			} else {
				processing = true;
			}
		}

		addListenerInternal(eventType, listener);
	}

	private void addListenerInternal(Object eventType, Object listener) {
		final ArrayExt<Object> listenersByType = getListenersByType(eventType);
		if (!listenersByType.contains(listener, true)) {
			listenersByType.add(listener);
			if (listener instanceof Ordered) {
				listenersByType.sort(ListenersComparator.instance);
			}
		}
		processPool();
	}

	private <LISTENER> ArrayExt<LISTENER> getListenersByType(Object eventType) {
		@SuppressWarnings("unchecked")
		ArrayExt<LISTENER> listenersByType = (ArrayExt<LISTENER>) listeners.get(eventType);

		if (listenersByType == null) {
			listenersByType = new ArrayExt<LISTENER>();
			listeners.put(eventType, listenersByType);
		}

		return listenersByType;
	}

	public <LISTENER> void removeListener(Class<? extends Event<LISTENER>> eventType, LISTENER listener) {
		remove(eventType, listener);
	}

	public <T> void removeListener(T eventType, Listener1<? super T> listener) {
		remove(eventType, listener);
	}

	private void remove(Object eventType, Object listener) {
		synchronized (pool) {
			if (processing) {
				EventBusAction eventBusAction = Pools.obtain(EventBusAction.class);
				eventBusAction.add = false;
				eventBusAction.eventType = eventType;
				eventBusAction.listener = listener;
				pool.add(eventBusAction);
				return;
			} else {
				processing = true;
			}
		}

		removeListenerInternal(eventType, listener);
	}

	private void removeListenerInternal(Object eventType, Object listener) {
		final ArrayExt<Object> listenersByType = getListenersByType(eventType);
		if (listenersByType != null) {
			listenersByType.removeValue(listener, true);
			if (listenersByType.size == 0) {
				listeners.remove(eventType);
			}
		}
		processPool();
	}

	private <LISTENER> ArrayExt<LISTENER> findListenersByType(Object eventType) {
		@SuppressWarnings("unchecked")
		ArrayExt<LISTENER> listenersByType = (ArrayExt<LISTENER>) listeners.get(eventType);
		return listenersByType;
	}

	public <LISTENER> void notify(final Event<LISTENER> event) {
		notifyInternal(event);
	}

	public void notify(Object event) {
		notifyInternal(event);
	}

	private void notifyInternal(Object event) {
		synchronized (pool) {
			if (processing) {
				pool.add(event);
				return;
			} else {
				processing = true;
			}
		}

		notifyListeners(event);
	}

	private void notifyListeners(Object event) {
		if (event instanceof Event) {
			@SuppressWarnings("unchecked")
			Event<Object> complexEvent = (Event<Object>) event;
			@SuppressWarnings("unchecked")
			Class<Event<Object>> eventType = (Class<Event<Object>>) complexEvent.getClass();
			final ArrayExt<?> listenersByType = findListenersByType(eventType);
			if (listenersByType == null) {
				return;
			}

			for (int i = 0; i < listenersByType.size; i++) {
				Object listener = listenersByType.get(i);
				complexEvent.notify(listener);
			}
		} else {
			final ArrayExt<?> listenersByType = findListenersByType(event);
			if (listenersByType == null) {
				return;
			}

			for (int i = 0; i < listenersByType.size; i++) {
				@SuppressWarnings("unchecked")
				Listener1<Object> listener = (Listener1<Object>) listenersByType.get(i);
				listener.handle(event);
			}
		}

		processPool();
	}

	private void processPool() {
		Object pooledValue = null;
		synchronized (pool) {
			if (pool.size > 0) {
				pooledValue = pool.removeIndex(0);
			} else {
				processing = false;
				return;
			}
		}

		if (pooledValue instanceof EventBusAction) {
			EventBusAction action = (EventBusAction) pooledValue;
			if (action.add) {
				addListenerInternal(action.eventType, action.listener);
			} else {
				removeListenerInternal(action.eventType, action.listener);
			}
			Pools.free(action);
		} else if (pooledValue == clear) {
			clearInternal();
		} else {
			notifyListeners(pooledValue);
		}
	}

	public <LISTENER> ImmutableArray<LISTENER> getListeners(Class<? extends Event<LISTENER>> eventType) {
		ArrayExt<LISTENER> listenersByType = findListenersByType(eventType);
		return listenersByType == null ? ImmutableArray.<LISTENER> empty() : listenersByType.immutable();
	}

	public <LISTENER> Array<LISTENER> getListeners(Class<? extends Event<LISTENER>> eventType, Array<LISTENER> out) {
		ArrayExt<LISTENER> listenersByType = findListenersByType(eventType);
		if (listenersByType != null) {
			out.addAll(listenersByType);
		}
		return out;
	}

	public <T> ImmutableArray<Listener1<? super T>> getListeners(T eventType) {
		ArrayExt<Listener1<? super T>> listenersByType = findListenersByType(eventType);
		return listenersByType == null ? ImmutableArray.<Listener1<? super T>> empty() : listenersByType.immutable();
	}

	public <T> Array<Listener1<? super T>> getListeners(T eventType, Array<Listener1<? super T>> out) {
		ArrayExt<Listener1<? super T>> listenersByType = findListenersByType(eventType);
		if (listenersByType != null) {
			out.addAll(listenersByType);
		}
		return out;
	}

	public boolean isEmpty() {
		return listeners.size == 0;
	}

	public void clear() {
		synchronized (pool) {
			if (processing) {
				pool.add(clear);
				return;
			} else {
				processing = true;
			}
		}

		clearInternal();
	}

	private void clearInternal() {
		listeners.clear();
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

	private static class EventBusAction implements Poolable {
		Object eventType;
		Object listener;
		boolean add;

		@Override
		public void reset() {
			eventType = null;
			listener = null;
		}
	}
}
