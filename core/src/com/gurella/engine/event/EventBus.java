package com.gurella.engine.event;

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

	void subscribe(Object subscriber, ObjectSet<Class<? extends EventSubscription>> subscriptions,
			SubscriberComparator comparator) {
		for (Class<? extends EventSubscription> subscription : subscriptions) {
			OrderedIdentitySet<Object> subscribers = listenersByType(subscription);
			if (subscribers.add(subscriber)) {
				size++;
				comparator.subscription = subscription;
				subscribers.sort(comparator);
			}
		}
	}

	private <L> OrderedIdentitySet<L> listenersByType(Class<? extends EventSubscription> subscription) {
		@SuppressWarnings("unchecked")
		OrderedIdentitySet<L> listenersByType = (OrderedIdentitySet<L>) listeners.get(subscription);
		if (listenersByType == null) {
			@SuppressWarnings("unchecked")
			OrderedIdentitySet<L> temp = PoolService.obtain(OrderedIdentitySet.class);
			listenersByType = temp;
			listeners.put(subscription, listenersByType);
		}
		return listenersByType;
	}

	void unsubscribe(Object subscriber, ObjectSet<Class<? extends EventSubscription>> subscriptions) {
		for (Class<? extends EventSubscription> subscription : subscriptions) {
			unsubscribe(subscriber, subscription);
		}
	}

	protected void unsubscribe(Object subscriber, Class<? extends EventSubscription> subscription) {
		OrderedIdentitySet<Object> subscribers = Values.cast(listeners.get(subscription));
		if (subscribers == null) {
			return;
		}

		if (subscribers.remove(subscriber)) {
			size--;
		}

		if (subscribers.size == 0) {
			PoolService.free(listeners.remove(subscription));
		}
	}

	<L extends EventSubscription> Array<? super L> getSubscribers(Class<L> subscriptionType, Array<? super L> out) {
		OrderedIdentitySet<L> listenersByType = Values.cast(listeners.get(subscriptionType));
		if (listenersByType != null) {
			listenersByType.orderedItems().appendTo(out);
		}
		return out;
	}

	@Override
	public void reset() {
		size = 0;
		for (OrderedIdentitySet<?> set : listeners.values()) {
			PoolService.free(set);
		}
		listeners.clear();
	}
}
