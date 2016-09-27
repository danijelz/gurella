package com.gurella.engine.event;

import static com.gurella.engine.event.Subscriptions.getSubscriptions;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.GurellaEngine;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.OrderedIdentitySet;
import com.gurella.engine.utils.Values;

//TODO unused
public class EventBus2 implements Poolable {
	private volatile int size;
	private final ObjectMap<Class<? extends EventSubscription>, OrderedIdentitySet<?>> listeners = new ObjectMap<Class<? extends EventSubscription>, OrderedIdentitySet<?>>();

	private final ArrayExt<Object> eventQueue = new ArrayExt<Object>(256);
	private final ArrayExt<Object> workingListeners = new ArrayExt<Object>(256);

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

	public <L extends EventSubscription> void post(Event0<L> event) {
		if (!GurellaEngine.isInRenderThread()) {
			synchronized (eventQueue) {
				eventQueue.add(event);
				return;
			}
		}

		synchronized (eventQueue) {
			if (processing) {
				eventQueue.add(event);
				return;
			} else {
				processing = true;
			}
		}

		if (eventQueue.size > 0) {
			eventQueue.add(event);
			processPool();
		} else {
			notifyListeners(event);
		}
	}

	private <L extends EventSubscription> void notifyListeners(Event0<L> event) {
		ArrayExt<L> listenersByType = getListenersByType(event);

		for (int i = 0; i < listenersByType.size; i++) {
			L listener = listenersByType.get(i);
			event.notify(listener);
		}

		listenersByType.clear();
		processPool();
	}

	private <L extends EventSubscription> ArrayExt<L> getListenersByType(final Event<L> event) {
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
		Event0<EventSubscription> event0 = null;

		synchronized (eventQueue) {
			if (eventQueue.size > 0) {
				@SuppressWarnings("unchecked")
				Event<EventSubscription> event = (Event<EventSubscription>) eventQueue.get(0);
				event0 = (Event0<EventSubscription>) event;
				eventQueue.removeIndex(0);
			} else {
				processing = false;
				return;
			}
		}

		notifyListeners(event0);
	}

	public <L extends EventSubscription> Array<? super L> getSubscribers(Class<L> type, Array<? super L> out) {
		return getSubscribersInternal(type, out);
	}

	private <T> Array<T> getSubscribersInternal(Class<? extends EventSubscription> eventType, Array<T> out) {
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
			synchronized (eventQueue) {
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
			synchronized (eventQueue) {
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
		eventQueue.clear();
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
