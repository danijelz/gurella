package com.gurella.engine.event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.pool.PoolService;

public class EventService {
	private static final EventBus globalEventBus = new EventBus();
	private static final IntMap<EventBus> channelEventBuses = new IntMap<EventBus>();

	public static void subscribe(Object subscriber) {
		globalEventBus.subscribe(subscriber);
	}

	public static void unsubscribe(Object subscriber) {
		globalEventBus.unsubscribe(subscriber);
	}

	public static <L extends EventSubscription> void post(Event<L> event) {
		globalEventBus.post(event);
	}

	public static <L extends EventSubscription> Array<? super L> getSubscribers(Class<L> subscriptionType,
			Array<? super L> out) {
		return globalEventBus.getSubscribers(subscriptionType, out);
	}

	public static void subscribe(int channel, Object subscriber) {
		if (Subscriptions.getSubscriptions(subscriber.getClass()).size == 0) {
			return;
		}
		synchronized (channelEventBuses) {
			EventBus eventBus = channelEventBuses.get(channel);
			if (eventBus == null) {
				eventBus = PoolService.obtain(EventBus.class);
				channelEventBuses.put(channel, eventBus);
			}
			eventBus.subscribe(subscriber);
		}
	}

	public static void unsubscribe(int channel, Object subscriber) {
		if (Subscriptions.getSubscriptions(subscriber.getClass()).size == 0) {
			return;
		}
		synchronized (channelEventBuses) {
			EventBus eventBus = channelEventBuses.get(channel);
			if (eventBus != null) {
				eventBus.unsubscribe(subscriber);
				if (eventBus.isEmpty()) {
					PoolService.free(eventBus);
				}
			}
		}
	}

	public static <L extends EventSubscription> void post(int channel, Event<L> event) {
		EventBus eventBus;
		synchronized (channelEventBuses) {
			eventBus = channelEventBuses.get(channel);
		}

		if (eventBus != null) {
			eventBus.post(event);
		}
	}

	public static <L extends EventSubscription> Array<? super L> getSubscribers(int channel, Class<L> subscriptionType,
			Array<? super L> out) {
		synchronized (channelEventBuses) {
			EventBus eventBus = channelEventBuses.get(channel);
			if (eventBus != null) {
				eventBus.getSubscribers(subscriptionType, out);
			}
		}
		return out;
	}
}
