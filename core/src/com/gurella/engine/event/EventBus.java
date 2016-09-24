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

	private <L> OrderedIdentitySet<L> listenersByType(Class<? extends EventSubscription> subscription) {
		@SuppressWarnings("unchecked")
		OrderedIdentitySet<L> listenersByType = (OrderedIdentitySet<L>) listeners.get(subscription);
		if (listenersByType == null) {
			listenersByType = new OrderedIdentitySet<L>();
			listeners.put(subscription, listenersByType);
		}
		return listenersByType;
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

	public <L extends EventSubscription, D> void notify(Event<L, D> event, D data) {
		synchronized (eventPool) {
			if (processing) {
				eventPool.add(event);
				eventPool.add(data);
				return;
			} else {
				processing = true;
			}
		}

		notifyListeners(event, data);
	}

	private <L extends EventSubscription, D> void notifyListeners(final Event<L, D> event, D data) {
		ArrayExt<L> listenersByType = getListenersByType(event);

		for (int i = 0; i < listenersByType.size; i++) {
			L listener = listenersByType.get(i);
			event.notify(listener, data);
		}

		listenersByType.clear();
		processPool();
	}

	public <L extends EventSubscription, D1, D2> void notify(Event2<L, D1, D2> event, D1 data1, D2 data2) {
		synchronized (eventPool) {
			if (processing) {
				eventPool.add(event);
				eventPool.add(data1);
				eventPool.add(data2);
				return;
			} else {
				processing = true;
			}
		}

		notifyListeners(event, data1, data2);
	}

	private <L extends EventSubscription, D1, D2> void notifyListeners(final Event2<L, D1, D2> event, D1 data1,
			D2 data2) {
		ArrayExt<L> listenersByType = getListenersByType(event);

		for (int i = 0; i < listenersByType.size; i++) {
			L listener = listenersByType.get(i);
			event.notify(listener, data1, data2);
		}

		listenersByType.clear();
		processPool();
	}

	private <L extends EventSubscription, D> ArrayExt<L> getListenersByType(final Event<L, D> event) {
		Class<L> eventType = event.getSubscriptionType();
		ArrayExt<L> listenersByType = Values.cast(workingListeners);
		synchronized (listeners) {
			OrderedIdentitySet<L> temp = Values.cast(listeners.get(eventType));
			if (temp != null) {
				temp.orderedItems().appendAll(listenersByType);
			}
		}
		return listenersByType;
	}

	private void processPool() {
		Event<EventSubscription, Object> event = null;
		Object data = null;
		synchronized (eventPool) {
			if (eventPool.size > 0) {
				@SuppressWarnings("unchecked")
				Event<EventSubscription, Object> casted = (Event<EventSubscription, Object>) eventPool.get(0);
				event = casted;
				data = eventPool.get(1);
				eventPool.removeRange(0, 1);
			} else {
				processing = false;
				return;
			}
		}

		notifyListeners(event, data);
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
