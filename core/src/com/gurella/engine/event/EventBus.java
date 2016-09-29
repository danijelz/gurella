package com.gurella.engine.event;

import static com.gurella.engine.event.Subscriptions.getSubscriptions;

import java.util.Comparator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.application.GurellaStateProvider;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.OrderedIdentitySet;
import com.gurella.engine.utils.Values;

public class EventBus implements Poolable {
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

	public <L extends EventSubscription> void post(Event<L> event) {
		boolean processPool = false;
		boolean inRenderThread = isInRenderThread();

		synchronized (eventQueue) {
			if (processing || !inRenderThread) {
				eventQueue.add(event);
				return;
			} else if (eventQueue.size > 0) {
				eventQueue.add(event);
				processPool = true;
			} else {
				processing = true;
			}
		}

		if (processPool) {
			processQueue();
		} else {
			dispatch(event);
		}
	}

	private static boolean isInRenderThread() {
		ApplicationListener listener = Gdx.app.getApplicationListener();
		if (listener instanceof GurellaStateProvider) {
			return ((GurellaStateProvider) listener).isInRenderThread();
		} else {
			return true;
		}
	}

	private <L extends EventSubscription> void dispatch(Event<L> event) {
		ArrayExt<L> listenersByType = getListenersByType(event);

		for (int i = 0; i < listenersByType.size; i++) {
			L listener = listenersByType.get(i);
			event.dispatch(listener);
		}

		listenersByType.clear();
		processQueue();
	}

	private <L extends EventSubscription> ArrayExt<L> getListenersByType(final Event<L> event) {
		Class<L> eventType = event.getSubscriptionType();
		ArrayExt<L> listenersByType = Values.cast(workingListeners);
		synchronized (listeners) {
			OrderedIdentitySet<L> temp = Values.cast(listeners.get(eventType));
			if (temp != null) {
				temp.appendTo(listenersByType);
			}
		}
		return listenersByType;
	}

	private void processQueue() {
		Event<EventSubscription> event;

		synchronized (eventQueue) {
			if (eventQueue.size > 0) {
				@SuppressWarnings("unchecked")
				Event<EventSubscription> casted = (Event<EventSubscription>) eventQueue.removeIndex(0);
				event = casted;
			} else {
				processing = false;
				return;
			}
		}

		dispatch(event);
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
