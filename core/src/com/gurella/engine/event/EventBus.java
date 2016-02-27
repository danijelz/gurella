package com.gurella.engine.event;

import static com.gurella.engine.event.Subscriptions.getSubscriptions;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.Values;

public class EventBus implements Poolable {
	private final ObjectMap<Object, Array<?>> listeners = new ObjectMap<Object, Array<?>>();

	private final Array<Object> eventPool = new Array<Object>();
	private final Array<Object> selectedListeners = new Array<Object>();

	private boolean processing;

	public <L> boolean addListener(Class<? extends Event<L>> eventType, L listener) {
		synchronized (listeners) {
			return addListenerInternal(eventType, listener);
		}
	}

	public <T> boolean addListener(T eventType, Listener1<? super T> listener) {
		synchronized (listeners) {
			return addListenerInternal(eventType, listener);
		}
	}

	private boolean addListenerInternal(Object event, Object listener) {
		final Array<Object> listenersByType = listenersByType(event);
		if (listenersByType.contains(listener, true)) {
			return false;
		} else {
			listenersByType.add(listener);
			Class<?> eventType = event.getClass();
			ListenersComparator comparator = PoolService.obtain(ListenersComparator.class);
			comparator.eventType = eventType;
			PoolService.free(comparator);
			return true;
		}
	}

	public void subscribe(Object subscriber) {
		ObjectSet<Class<? extends EventSubscription>> subscriptions = getSubscriptions(subscriber.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		SubscriberComparator comparator = PoolService.obtain(SubscriberComparator.class);
		synchronized (listeners) {
			for (Class<? extends EventSubscription> subscription : subscriptions) {
				Array<Object> subscribers = listenersByType(subscription);
				subscribers.add(subscriber);
				comparator.subscription = subscription;
				subscribers.sort(comparator);
			}
		}

		PoolService.free(comparator);
	}

	private <L> Array<L> listenersByType(Object eventType) {
		Array<L> listenersByType = Values.cast(listeners.get(eventType));
		if (listenersByType == null) {
			listenersByType = new Array<L>();
			listeners.put(eventType, listenersByType);
		}
		return listenersByType;
	}

	public <L> boolean removeListener(Class<? extends Event<L>> eventType, L listener) {
		synchronized (listeners) {
			return removeListenerInternal(eventType, listener);
		}
	}

	public <T> boolean removeListener(T eventType, Listener1<? super T> listener) {
		synchronized (listeners) {
			return removeListenerInternal(eventType, listener);
		}
	}

	private boolean removeListenerInternal(Object eventType, Object listener) {
		Array<Object> listenersByType = Values.cast(listeners.get(eventType));
		return listenersByType == null ? false : listenersByType.removeValue(listener, true);
	}

	public void unsubscribe(Object subscriber) {
		ObjectSet<Class<? extends EventSubscription>> subscriptions = getSubscriptions(subscriber.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		synchronized (listeners) {
			for (Class<? extends EventSubscription> subscription : subscriptions) {
				Array<Object> subscribers = Values.cast(listeners.get(subscription));
				if (subscribers != null) {
					subscribers.removeValue(subscriber, true);
				}
			}
		}
	}

	public <L> void notify(final Event<L> event) {
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
				return;
			} else {
				processing = true;
			}
		}

		notifyListeners(eventType);
	}

	private <L> void notifyListeners(final Event<L> event) {
		Class<? extends Event<L>> eventType = Values.cast(event.getClass());
		Array<L> listenersByType = Values.cast(selectedListeners);
		synchronized (listeners) {
			Array<L> temp = Values.cast(listeners.get(eventType));
			if (temp == null) {
				return;
			}
			listenersByType.addAll(temp);
		}

		for (int i = 0; i < listenersByType.size; i++) {
			L listener = listenersByType.get(i);
			event.notify(listener);
		}

		listenersByType.clear();

		processPool();
	}

	private <T> void notifyListeners(T eventType) {
		Array<Listener1<T>> listenersByType = Values.cast(selectedListeners);
		synchronized (listeners) {
			Array<Listener1<T>> temp = Values.cast(listeners.get(eventType));
			if (temp == null) {
				return;
			}

			listenersByType.addAll(temp);
		}

		for (int i = 0; i < listenersByType.size; i++) {
			Listener1<T> listener = listenersByType.get(i);
			listener.handle(eventType);
		}
		listenersByType.clear();

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

	public <L> Array<? super L> getListeners(Class<? extends Event<L>> eventType, Array<? super L> out) {
		return getListenersInternal(eventType, out);
	}

	public <T> Array<Listener1<? super T>> getListeners(T eventType, Array<Listener1<? super T>> out) {
		return getListenersInternal(eventType, out);
	}

	public <L extends EventSubscription> Array<? super L> getSubscribers(Class<L> type, Array<? super L> out) {
		return getListenersInternal(type, out);
	}

	public <T> Array<T> getListenersInternal(Object eventType, Array<T> out) {
		synchronized (listeners) {
			Array<T> listenersByType = Values.cast(listeners.get(eventType));
			if (listenersByType != null) {
				out.addAll(listenersByType);
			}
			return out;
		}
	}

	public boolean isEmpty() {
		while (true) {
			synchronized (eventPool) {
				if (!processing) {
					//TODO wrong
					for (Array<?> listenersByType : listeners.values()) {
						if (listenersByType.size > 0) {
							return false;
						}
					}
					return true;
				}
			}
			ThreadUtils.yield();
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

	private static class ListenersComparator implements Comparator<Object>, Poolable {
		Class<?> eventType;

		@Override
		public int compare(Object o1, Object o2) {
			return Values.compare(Listeners.getPriority(eventType, o1.getClass()),
					Listeners.getPriority(eventType, o2.getClass()));
		}

		@Override
		public void reset() {
			eventType = null;
		}
	}

	private static class SubscriberComparator implements Comparator<Object>, Poolable {
		Class<? extends EventSubscription> subscription;

		@Override
		public int compare(Object o1, Object o2) {
			return Values.compare(Subscriptions.getPriority(o1.getClass(), subscription),
					Subscriptions.getPriority(o2.getClass(), subscription));
		}

		@Override
		public void reset() {
			subscription = null;
		}
	}
}
