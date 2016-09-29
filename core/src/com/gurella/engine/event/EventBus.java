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
import com.gurella.engine.pool.ObjectArrayPool;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.OrderedIdentitySet;
import com.gurella.engine.utils.Values;

public class EventBus implements Poolable {
	private volatile int size;

	private final ObjectMap<Class<? extends EventSubscription>, OrderedIdentitySet<?>> listeners = new ObjectMap<Class<? extends EventSubscription>, OrderedIdentitySet<?>>();

	private ArrayExt<Event<?>> eventQueue = new ArrayExt<Event<?>>(256);
	private ArrayExt<Event<?>> workingEvents = new ArrayExt<Event<?>>(256);

	private ObjectArrayPool<Object> subscribersPool = new SubscribersPool(Object.class);

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
		if (inRenderThread()) {
			dispatch(event);
		} else {
			synchronized (eventQueue) {
				eventQueue.add(event);
			}
		}
	}

	private static boolean inRenderThread() {
		ApplicationListener listener = Gdx.app.getApplicationListener();
		if (listener instanceof GurellaStateProvider) {
			return ((GurellaStateProvider) listener).isInRenderThread();
		} else {
			return true;
		}
	}

	public void drain() {
		if (!inRenderThread()) {
			return;
		}

		synchronized (eventQueue) {
			ArrayExt<Event<?>> workingEventQueue = eventQueue;
			eventQueue = workingEvents;
			workingEvents = workingEventQueue;
		}

		for (int i = 0; i < workingEvents.size; i++) {
			dispatch(workingEvents.get(i));
		}

		workingEvents.clear();
	}

	private <L extends EventSubscription> void dispatch(Event<L> event) {
		Class<L> subscriptionType = event.getSubscriptionType();
		Object[] listenersByType;
		int listenersSize;

		synchronized (listeners) {
			@SuppressWarnings("unchecked")
			OrderedIdentitySet<Object> temp = (OrderedIdentitySet<Object>) listeners.get(subscriptionType);
			if (temp == null || temp.size == 0) {
				return;
			}

			listenersSize = temp.size;
			listenersByType = subscribersPool.obtain(listenersSize, Integer.MAX_VALUE);
			temp.toArray(listenersByType);
			System.arraycopy(temp, 0, listenersByType, 0, listenersSize);
		}

		for (int i = 0; i < listenersSize; i++) {
			@SuppressWarnings("unchecked")
			L listener = (L) listenersByType[i];
			event.dispatch(listener);
		}

		subscribersPool.free(listenersByType);
		processQueue();
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
		workingEvents.clear();
		processing = false;
		// TODO listeners.reset(), eventQueue.reset(), workingListeners.reset()
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

	private static final class SubscribersPool extends ObjectArrayPool<Object> {
		private SubscribersPool(Class<Object> componentType) {
			super(componentType);
		}

		@Override
		protected Object[] newObject(int length) {
			return new Object[length];
		}
	}
}
