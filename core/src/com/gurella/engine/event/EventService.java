package com.gurella.engine.event;

import static com.gurella.engine.event.Subscriptions.getSubscriptions;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.OrderedIdentitySet;

//TODO add closeChannel()
public class EventService {
	private static final EventBus global = new EventBus();
	private static final SubscriberComparator globalComparator = new SubscriberComparator();
	private static final IntMap<EventBus> channels = new IntMap<EventBus>();
	private static final SubscriberComparator channelsComparator = new SubscriberComparator();

	private EventService() {
	}

	public static void subscribe(Object subscriber) {
		ObjectSet<Class<? extends EventSubscription>> subscriptions = getSubscriptions(subscriber.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		synchronized (global) {
			global.subscribe(subscriber, subscriptions, globalComparator);
		}
	}

	public static void unsubscribe(Object subscriber) {
		ObjectSet<Class<? extends EventSubscription>> subscriptions = getSubscriptions(subscriber.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		synchronized (global) {
			global.unsubscribe(subscriber, subscriptions);
		}
	}

	public static <L extends EventSubscription> void post(Event<L> event) {
		post(event.getSubscriptionType(), event);
	}

	public static <L extends EventSubscription> void post(Class<L> subscriptionType, Dispatcher<L> dispatcher) {
		Object[] listenersByType;
		int listenersSize;

		synchronized (global) {
			@SuppressWarnings("unchecked")
			OrderedIdentitySet<Object> temp = (OrderedIdentitySet<Object>) global.listeners.get(subscriptionType);
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
			dispatcher.dispatch(listener);
		}

		PoolService.free(listenersByType);
	}

	public static <L extends EventSubscription> Array<? super L> getSubscribers(Class<L> subscriptionType,
			Array<? super L> out) {
		synchronized (global) {
			return global.getSubscribers(subscriptionType, out);
		}
	}

	public static void subscribe(int channel, Object subscriber) {
		ObjectSet<Class<? extends EventSubscription>> subscriptions = getSubscriptions(subscriber.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		synchronized (channels) {
			EventBus eventBus = channels.get(channel);
			if (eventBus == null) {
				eventBus = PoolService.obtain(EventBus.class);
				channels.put(channel, eventBus);
			}
			eventBus.subscribe(subscriber, subscriptions, channelsComparator);
		}
	}

	public static void unsubscribe(int channel, Object subscriber) {
		ObjectSet<Class<? extends EventSubscription>> subscriptions = getSubscriptions(subscriber.getClass());
		if (subscriptions.size == 0) {
			return;
		}

		EventBus emptyEventBus = null;
		synchronized (channels) {
			EventBus eventBus = channels.get(channel);
			if (eventBus == null) {
				return;
			}

			eventBus.unsubscribe(subscriber, subscriptions);
			if (eventBus.size == 0) {
				emptyEventBus = eventBus;
			}
		}

		if (emptyEventBus != null) {
			PoolService.free(emptyEventBus);
		}
	}

	public static <L extends EventSubscription> void post(int channel, Event<L> event) {
		post(channel, event.getSubscriptionType(), event);
	}

	public static <L extends EventSubscription> void post(int channel, Class<L> subscriptionType,
			Dispatcher<L> dispatcher) {
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
			dispatcher.dispatch(listener);
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
