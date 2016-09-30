package com.gurella.engine.event;

import static com.gurella.engine.event.Subscriptions.getSubscriptions;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.OrderedIdentitySet;
import com.gurella.engine.utils.Values;

class EventBus implements Poolable {
	volatile int size;
	final ObjectMap<Class<? extends EventSubscription>, OrderedIdentitySet<?>> listeners = new ObjectMap<Class<? extends EventSubscription>, OrderedIdentitySet<?>>();

	public void subscribe(Object subscriber) {
		ObjectSet<Class<? extends EventSubscription>> subscriptions = getSubscriptions(subscriber.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		subscribe(subscriber, subscriptions);
	}

	void subscribe(Object subscriber, ObjectSet<Class<? extends EventSubscription>> subscriptions) {
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

	public <L extends EventSubscription> Array<? super L> getSubscribers(Class<L> subscriptionType,
			Array<? super L> out) {
		synchronized (listeners) {
			OrderedIdentitySet<L> listenersByType = Values.cast(listeners.get(subscriptionType));
			if (listenersByType != null) {
				listenersByType.orderedItems().appendTo(out);
			}
			return out;
		}
	}

	@Override
	public void reset() {
		size = 0;
		listeners.clear();
		// TODO listeners.reset()
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
