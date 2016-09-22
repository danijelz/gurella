package com.gurella.engine.event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.pool.PoolService;

public class EventService {
	private static final EventBus globalEventBus = new EventBus();
	private static final IntMap<EventBus> channelEventBuses = new IntMap<EventBus>();

	public static <L extends EventSubscription, D> void notify(Event<L, D> event, D data) {
		globalEventBus.notify(event, data);
	}

	public static void subscribe(Object subscriber) {
		globalEventBus.subscribe(subscriber);
	}

	public static void unsubscribe(Object subscriber) {
		globalEventBus.unsubscribe(subscriber);
	}

	public static <L extends EventSubscription> Array<? super L> getSubscribers(Class<L> subscriptionType,
			Array<? super L> out) {
		return globalEventBus.getSubscribers(subscriptionType, out);
	}

	private static EventBus getEventBusByChannel(int channel) {
		synchronized (channelEventBuses) {
			EventBus eventBus = channelEventBuses.get(channel);
			if (eventBus == null) {
				eventBus = PoolService.obtain(EventBus.class);
				channelEventBuses.put(channel, eventBus);
			}
			return eventBus;
		}
	}

	public static void subscribe(int channel, Object subscriber) {
		if (Subscriptions.getSubscriptions(subscriber.getClass()).size == 0) {
			return;
		}
		getEventBusByChannel(channel).subscribe(subscriber);
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

	public static <L extends EventSubscription, D> void notify(int channel, Event<L, D> event, D data) {
		EventBus eventBus;
		synchronized (channelEventBuses) {
			eventBus = channelEventBuses.get(channel);
		}

		if (eventBus != null) {
			eventBus.notify(event, data);
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
