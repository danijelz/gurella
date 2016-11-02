package com.gurella.engine.event;

import static com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom;
import static com.gurella.engine.utils.Reflection.getAnnotation;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.utils.Values;
import com.gurella.engine.utils.priority.Priority;
import com.gurella.engine.utils.priority.TypePriorities;
import com.gurella.engine.utils.priority.TypePriority;

//TODO unify priorities with EventBus
public final class Subscriptions {
	private static final ObjectMap<Class<?>, ObjectSet<Class<? extends EventSubscription>>> subscriptions = new ObjectMap<Class<?>, ObjectSet<Class<? extends EventSubscription>>>();
	private static final ObjectMap<Class<?>, ObjectIntMap<Class<?>>> priorities = new ObjectMap<Class<?>, ObjectIntMap<Class<?>>>();

	private static final Object mutex = new Object();

	static {
		subscriptions.put(Object.class, new ObjectSet<Class<? extends EventSubscription>>());
		subscriptions.put(EventSubscription.class, new ObjectSet<Class<? extends EventSubscription>>());
		priorities.put(Object.class, new ObjectIntMap<Class<?>>());
		priorities.put(EventSubscription.class, new ObjectIntMap<Class<?>>());
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

		ObjectIntMap<Class<?>> listenerPriorities = new ObjectIntMap<Class<?>>();
		Class<?> superclass = subscriberType.getSuperclass();
		if (superclass != null) {
			initCaches(superclass);
			subscriptionsByType.addAll(subscriptions.get(superclass));
			listenerPriorities.putAll(priorities.get(superclass));
		}

		Priority priority = getAnnotation(subscriberType, Priority.class);
		TypePriority typePriority = getAnnotation(subscriberType, TypePriority.class);
		TypePriorities typePriorities = getAnnotation(subscriberType, TypePriorities.class);
		if (priority != null || typePriority != null || typePriorities != null) {
			for (Class<? extends EventSubscription> subscription : subscriptionsByType) {
				if (typePriority != null && subscription.getClass().equals(typePriority.type())) {
					listenerPriorities.put(subscription, typePriority.priority());
				} else {
					TypePriority subscriptionPriority = findSubscriptionPriority(typePriorities, subscription);
					if (subscriptionPriority != null) {
						listenerPriorities.put(subscription, subscriptionPriority.priority());
					} else if (priority != null) {
						listenerPriorities.put(subscription, priority.value());
					}
				}
			}
		}

		subscriptions.put(subscriberType, subscriptionsByType);
		priorities.put(subscriberType, listenerPriorities);
	}

	private static TypePriority findSubscriptionPriority(TypePriorities priorities,
			Class<? extends EventSubscription> subscription) {
		if (priorities == null) {
			return null;
		}

		TypePriority[] values = priorities.value();
		for (int i = 0; i < values.length; i++) {
			TypePriority typePriority = values[i];
			if (subscription.getClass().equals(typePriority.type())) {
				return typePriority;
			}
		}
		return null;
	}

	static int getPriority(Class<?> type, Class<? extends EventSubscription> subscriptionType) {
		ObjectIntMap<Class<?>> prioritiesByListnerType = priorities.get(type);
		return prioritiesByListnerType == null ? 0 : prioritiesByListnerType.get(subscriptionType, 0);
	}
}
