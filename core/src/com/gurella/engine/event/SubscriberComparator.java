package com.gurella.engine.event;

import java.util.Comparator;

import com.gurella.engine.utils.Values;

class SubscriberComparator implements Comparator<Object> {
	Class<? extends EventSubscription> subscription;

	@Override
	public int compare(Object o1, Object o2) {
		return Values.compare(Subscriptions.getPriority(o1.getClass(), subscription),
				Subscriptions.getPriority(o2.getClass(), subscription));
	}
}