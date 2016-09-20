package com.gurella.engine.event;

import static com.gurella.engine.event.Subscriptions.getSubscriptions;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.OrderedIdentitySet;
import com.gurella.engine.utils.Values;

public class EventBus implements Poolable {
	private volatile int size;

	private final ObjectMap<Object, OrderedIdentitySet<?>> listeners = new ObjectMap<Object, OrderedIdentitySet<?>>();

	private final ArrayExt<Object> eventPool = new ArrayExt<Object>();
	private final ArrayExt<Object> workingListeners = new ArrayExt<Object>();

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
		final OrderedIdentitySet<Object> listenersByType = listenersByType(event);
		if (listenersByType.add(listener)) {
			size++;
			Class<?> eventType = event.getClass();
			ListenersComparator comparator = PoolService.obtain(ListenersComparator.class);
			comparator.eventType = eventType;
			listenersByType.sort(comparator);
			PoolService.free(comparator);
			return true;
		} else {
			return false;
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
				OrderedIdentitySet<Object> subscribers = listenersByType(subscription);
				if (subscribers.add(subscriber)) {
					size++;
					comparator.subscription = subscription;
					subscribers.sort(comparator);
				}
			}
		}
		PoolService.free(comparator);
	}

	private <L> OrderedIdentitySet<L> listenersByType(Object eventType) {
		OrderedIdentitySet<L> listenersByType = Values.cast(listeners.get(eventType));
		if (listenersByType == null) {
			listenersByType = new OrderedIdentitySet<L>();
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
		OrderedIdentitySet<Object> listenersByType = Values.cast(listeners.get(eventType));
		if (listenersByType != null && listenersByType.remove(listener)) {
			size--;
			return true;
		} else {
			return false;
		}
	}

	public void unsubscribe(Object subscriber) {
		ObjectSet<Class<? extends EventSubscription>> subscriptions = getSubscriptions(subscriber.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		synchronized (listeners) {
			for (Class<? extends EventSubscription> subscription : subscriptions) {
				OrderedIdentitySet<Object> subscribers = Values.cast(listeners.get(subscription));
				if (subscribers != null && subscribers.remove(subscriber)) {
					size--;
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

	public <L extends EventSubscription> void notify(SubscriptionEvent<L> event) {
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

	private <L> void notifyListeners(final Event<L> event) {
		Class<? extends Event<L>> eventType = Values.cast(event.getClass());
		ArrayExt<L> listenersByType = Values.cast(workingListeners);
		synchronized (listeners) {
			OrderedIdentitySet<L> temp = Values.cast(listeners.get(eventType));
			if (temp != null) {
				temp.orderedItems().appendAll(listenersByType);
			}
		}

		for (int i = 0; i < listenersByType.size; i++) {
			L listener = listenersByType.get(i);
			event.notify(listener);
		}

		listenersByType.clear();
		if (event instanceof Poolable) {
			PoolService.free(event);
		}

		processPool();
	}

	private <L extends EventSubscription> void notifyListeners(final SubscriptionEvent<L> event) {
		Class<L> eventType = event.subscriptionType;
		ArrayExt<L> listenersByType = Values.cast(workingListeners);
		synchronized (listeners) {
			OrderedIdentitySet<L> temp = Values.cast(listeners.get(eventType));
			if (temp != null) {
				temp.orderedItems().appendAll(listenersByType);
			}
		}

		for (int i = 0; i < listenersByType.size; i++) {
			L listener = listenersByType.get(i);
			event.notify(listener);
		}

		listenersByType.clear();
		if (event instanceof Poolable) {
			PoolService.free(event);
		}

		processPool();
	}

	private <T> void notifyListeners(T eventType) {
		ArrayExt<Listener1<T>> listenersByType = Values.cast(workingListeners);
		synchronized (listeners) {
			OrderedIdentitySet<Listener1<T>> temp = Values.cast(listeners.get(eventType));
			if (temp == null) {
				temp.orderedItems().appendAll(listenersByType);
			}
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

		if (event instanceof SubscriptionEvent) {
			notifyListeners((SubscriptionEvent<?>) event);
		} else if (event instanceof Event) {
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

	private <T> Array<T> getListenersInternal(Object eventType, Array<T> out) {
		synchronized (listeners) {
			OrderedIdentitySet<T> listenersByType = Values.cast(listeners.get(eventType));
			if (listenersByType != null) {
				listenersByType.orderedItems().appendAll(out);
			}
			return out;
		}
	}

	public boolean isEmpty() {
		while (true) {
			synchronized (eventPool) {
				if (!processing) {
					return size == 0;
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
		size = 0;
		listeners.clear();
		eventPool.clear();
		workingListeners.clear();
		processing = false;
		// TODO listeners.reset(), eventPool.reset(), workingListeners.reset()
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
