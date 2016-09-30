package com.gurella.engine.event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.OrderedIdentitySet;

public class EventService {
	private static final EventBus globalEventBus = new EventBus();
	private static final IntMap<EventBus> channels = new IntMap<EventBus>();

	private EventService() {
	}

	public static void subscribe(Object subscriber) {
		globalEventBus.subscribe(subscriber);
	}

	public static void unsubscribe(Object subscriber) {
		globalEventBus.unsubscribe(subscriber);
	}

	public static <L extends EventSubscription> void post(Event<L> event) {
		Class<L> subscriptionType = event.getSubscriptionType();
		Object[] listenersByType;
		int listenersSize;

		synchronized (globalEventBus) {
			@SuppressWarnings("unchecked")
			OrderedIdentitySet<Object> temp = (OrderedIdentitySet<Object>) globalEventBus.listeners
					.get(subscriptionType);
			if (temp == null || temp.size == 0) {
				return;
			}

			listenersSize = temp.size;
			listenersByType = PoolService.obtainObjectArray(listenersSize, Integer.MAX_VALUE);
			temp.toArray(listenersByType);
		}

		for (int i = 0; i < listenersSize; i++) {
			@SuppressWarnings("unchecked")
			L listener = (L) listenersByType[i];
			event.dispatch(listener);
		}

		PoolService.free(listenersByType);
	}

	public static <L extends EventSubscription> Array<? super L> getSubscribers(Class<L> subscriptionType,
			Array<? super L> out) {
		return globalEventBus.getSubscribers(subscriptionType, out);
	}

	public static void subscribe(int channel, Object subscriber) {
		ObjectSet<Class<? extends EventSubscription>> subscriptions = Subscriptions
				.getSubscriptions(subscriber.getClass());
		if (subscriptions.size == 0) {
			return;
		}
		synchronized (channels) {
			EventBus eventBus = channels.get(channel);
			if (eventBus == null) {
				eventBus = PoolService.obtain(EventBus.class);
				channels.put(channel, eventBus);
			}
			eventBus.subscribe(subscriber, subscriptions);
		}
	}

	public static void unsubscribe(int channel, Object subscriber) {
		synchronized (channels) {
			EventBus eventBus = channels.get(channel);
			if (eventBus != null) {
				eventBus.unsubscribe(subscriber);
				if (eventBus.size == 0) {
					PoolService.free(eventBus);
				}
			}
		}
	}

	public static <L extends EventSubscription> void post(int channel, Event<L> event) {
		Class<L> subscriptionType = event.getSubscriptionType();
		Object[] listenersByType;
		int listenersSize;

		synchronized (channels) {
			EventBus eventBus = channels.get(channel);
			if (eventBus == null) {
				return;
			}

			@SuppressWarnings("unchecked")
			OrderedIdentitySet<Object> temp = (OrderedIdentitySet<Object>) eventBus.listeners.get(subscriptionType);
			if (temp == null || temp.size == 0) {
				return;
			}

			listenersSize = temp.size;
			listenersByType = PoolService.obtainObjectArray(listenersSize, Integer.MAX_VALUE);
			temp.toArray(listenersByType);
		}

		for (int i = 0; i < listenersSize; i++) {
			@SuppressWarnings("unchecked")
			L listener = (L) listenersByType[i];
			event.dispatch(listener);
		}

		PoolService.free(listenersByType);
	}

	public static <L extends EventSubscription> Array<? super L> getSubscribers(int channel, Class<L> subscriptionType,
			Array<? super L> out) {
		synchronized (channels) {
			EventBus eventBus = channels.get(channel);
			if (eventBus != null) {
				eventBus.getSubscribers(subscriptionType, out);
			}
		}
		return out;
	}
}
