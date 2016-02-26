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
	private final ObjectMap<Class<?>, Array<Object>> subscribers = new ObjectMap<Class<?>, Array<Object>>();

	private final Array<Object> eventPool = new Array<Object>();

	private boolean processing;

	public <L> boolean addListener(Class<? extends Event<L>> eventType, L listener) {
		return addListenerInternal(eventType, listener);
	}

	public <T> boolean addListener(T eventType, Listener1<? super T> listener) {
		return addListenerInternal(eventType, listener);
	}

	private boolean addListenerInternal(Object event, Object listener) {
		final Array<Object> listenersByType = listenersByType(event);

		synchronized (listenersByType) {
			if (listenersByType.contains(listener, true)) {
				return false;
			} else {
				listenersByType.add(listener);
				Class<?> eventType = event.getClass();
				Listeners.initListenerPriotiy(eventType, listener.getClass());
				ListenersComparator comparator = PoolService.obtain(ListenersComparator.class);
				comparator.eventType = eventType;
				PoolService.free(comparator);
				return true;
			}
		}
	}

	private <L> Array<L> listenersByType(Object eventType) {
		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			Array<L> listenersByType = (Array<L>) listeners.get(eventType);

			if (listenersByType == null) {
				listenersByType = new Array<L>();
				listeners.put(eventType, listenersByType);
			}

			return listenersByType;
		}
	}

	public void subscribe(Object subscriber) {
		ObjectSet<Class<? extends EventSubscription>> subscriptions = getSubscriptions(subscriber.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		SubscriberComparator comparator = PoolService.obtain(SubscriberComparator.class);
		for (Class<? extends EventSubscription> subscription : subscriptions) {
			Array<Object> subscribers = findSubscribers(subscription);
			synchronized (subscribers) {
				subscribers.add(subscriber);
				comparator.subscription = subscription;
				subscribers.sort(comparator);
			}
		}
		PoolService.free(comparator);
	}

	private Array<Object> findSubscribers(Class<? extends EventSubscription> subscription) {
		synchronized (subscribers) {
			Array<Object> listeners = subscribers.get(subscription);
			if (listeners == null) {
				listeners = new Array<Object>();
				subscribers.put(subscription, listeners);
			}
			return listeners;
		}
	}

	public <L> boolean removeListener(Class<? extends Event<L>> eventType, L listener) {
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

	public void unsubscribe(Object subscriber) {
		ObjectSet<Class<? extends EventSubscription>> subscriptions = getSubscriptions(subscriber.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		for (Class<? extends EventSubscription> subscription : subscriptions) {
			synchronized (subscribers) {
				Array<Object> listeners = subscribers.get(subscription);
				if (listeners != null) {
					synchronized (listeners) {
						listeners.removeValue(subscriber, true);
						if (listeners.size == 0) {
							subscribers.remove(subscription);
						}
					}
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
			} else {
				processing = true;
			}
		}

		notifyListeners(eventType);
	}

	private <L> void notifyListeners(final Event<L> event) {
		Class<? extends Event<L>> eventType = Values.cast(event.getClass());
		Array<L> listenersByType;
		synchronized (listeners) {
			listenersByType = Values.cast(listeners.get(eventType));
			if (listenersByType == null) {
				return;
			}
		}

		synchronized (listenersByType) {
			for (int i = 0; i < listenersByType.size; i++) {
				L listener = listenersByType.get(i);
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

	public <L> Array<? super L> getListeners(Class<? extends Event<L>> eventType, Array<? super L> out) {
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

	public <L extends EventSubscription> Array<? super L> getSubscribers(Class<L> subscriptionType,
			Array<? super L> out) {
		@SuppressWarnings("unchecked")
		Array<Object> casted = (Array<Object>) out;
		synchronized (subscribers) {
			Array<Object> subscribersByType = subscribers.get(subscriptionType);
			if (subscribersByType != null) {
				casted.addAll(subscribersByType);
			}
		}
		return out;
	}

	public boolean isEmpty() {
		while (true) {
			synchronized (eventPool) {
				if (!processing) {
					return listeners.size == 0 && subscribers.size == 0;
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
		subscribers.clear();
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
