package com.gurella.engine.event;

import static com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.utils.Values;

public final class Subscriptions {
	private static final ObjectMap<Class<?>, ObjectSet<Class<? extends EventSubscription>>> subscriptions = new ObjectMap<Class<?>, ObjectSet<Class<? extends EventSubscription>>>();

	private static final Object mutex = new Object();

	static {
		subscriptions.put(Object.class, new ObjectSet<Class<? extends EventSubscription>>());
		subscriptions.put(EventSubscription.class, new ObjectSet<Class<? extends EventSubscription>>());
	}

	private Subscriptions() {
	}

	static ObjectSet<Class<? extends EventSubscription>> getSubscriptions(Class<?> subscriberType) {
		synchronized (mutex) {
			ObjectSet<Class<? extends EventSubscription>> subscriptionsByType = subscriptions.get(subscriberType);
			if (subscriptionsByType != null) {
				return subscriptionsByType;
			}
			initCaches(subscriberType);
			return subscriptions.get(subscriberType);
		}
	}

	private static void initCaches(Class<?> subscriberType) {
		if (subscriberType == Object.class || subscriptions.containsKey(subscriberType)) {
			return;
		}

		ObjectSet<Class<? extends EventSubscription>> subscriptionsByType = new ObjectSet<Class<? extends EventSubscription>>();
		Class<?>[] interfaces = ClassReflection.getInterfaces(subscriberType);
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> listenerInterface = interfaces[i];
			initCaches(listenerInterface);
			if (listenerInterface != EventSubscription.class
					&& isAssignableFrom(EventSubscription.class, listenerInterface)) {
				Class<? extends EventSubscription> casted = Values.cast(listenerInterface);
				subscriptionsByType.add(casted);
			}
		}

		Class<?> superclass = subscriberType.getSuperclass();
		if (superclass != null) {
			initCaches(superclass);
			subscriptionsByType.addAll(subscriptions.get(superclass));
		}

		subscriptions.put(subscriberType, subscriptionsByType);
	}
}
