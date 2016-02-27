package com.gurella.engine.event;

import static com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom;
import static com.gurella.engine.utils.Reflection.getAnnotation;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.utils.Values;

//TODO unify priorities with EventBus
public final class Subscriptions {
	private static final ObjectMap<Class<?>, ObjectSet<Class<? extends EventSubscription>>> subscriptions = new ObjectMap<Class<?>, ObjectSet<Class<? extends EventSubscription>>>();
	private static final ObjectMap<Class<?>, ObjectIntMap<Class<?>>> priorities = new ObjectMap<Class<?>, ObjectIntMap<Class<?>>>();

	private static final Object lock = new Object();

	static {
		subscriptions.put(Object.class, new ObjectSet<Class<? extends EventSubscription>>());
		subscriptions.put(EventSubscription.class, new ObjectSet<Class<? extends EventSubscription>>());
		priorities.put(Object.class, new ObjectIntMap<Class<?>>());
		priorities.put(EventSubscription.class, new ObjectIntMap<Class<?>>());
	}

	private Subscriptions() {
	}

	static ObjectSet<Class<? extends EventSubscription>> getSubscriptions(Class<?> listenerType) {
		synchronized (lock) {
			ObjectSet<Class<? extends EventSubscription>> impementedSubscribers = subscriptions.get(listenerType);
			if (impementedSubscribers != null) {
				return impementedSubscribers;
			}
			initCaches(listenerType);
			return subscriptions.get(listenerType);
		}
	}

	private static void initCaches(Class<?> listenerType) {
		if (listenerType == Object.class || subscriptions.containsKey(listenerType)) {
			return;
		}

		ObjectSet<Class<? extends EventSubscription>> listenerSubscriptions = new ObjectSet<Class<? extends EventSubscription>>();
		Class<?>[] interfaces = ClassReflection.getInterfaces(listenerType);
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> listenerInterface = interfaces[i];
			initCaches(listenerInterface);
			if (listenerInterface != EventSubscription.class
					&& isAssignableFrom(EventSubscription.class, listenerInterface)) {
				Class<? extends EventSubscription> casted = Values.cast(listenerInterface);
				listenerSubscriptions.add(casted);
			}
		}

		ObjectIntMap<Class<?>> listenerPriorities = new ObjectIntMap<Class<?>>();
		Class<?> superclass = listenerType.getSuperclass();
		if (superclass != null) {
			initCaches(superclass);
			listenerSubscriptions.addAll(subscriptions.get(superclass));
			listenerPriorities.putAll(priorities.get(superclass));
		}

		Priority priority = getAnnotation(listenerType, Priority.class);
		TypePriorities typePriorities = getAnnotation(listenerType, TypePriorities.class);
		if (priority != null || typePriorities != null) {
			for (Class<? extends EventSubscription> subscription : listenerSubscriptions) {
				TypePriority subscriptionPriority = findSubscriptionPriority(typePriorities, subscription);
				if (subscriptionPriority != null) {
					listenerPriorities.put(subscription, subscriptionPriority.priority());
				} else if (priority != null) {
					listenerPriorities.put(subscription, priority.value());
				}
			}
		}

		subscriptions.put(listenerType, listenerSubscriptions);
		priorities.put(listenerType, listenerPriorities);
	}

	private static TypePriority findSubscriptionPriority(TypePriorities typePriorities,
			Class<? extends EventSubscription> subscription) {
		if (typePriorities == null) {
			return null;
		}

		TypePriority[] values = typePriorities.value();
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

	public static void main(String[] args) {
		initCaches(B.class);
		subscriptions.get(I.class).iterator().toArray();
		subscriptions.get(J.class).iterator().toArray();
		subscriptions.get(A.class).iterator().toArray();
		subscriptions.get(B.class).iterator().toArray();
	}

	public interface I extends EventSubscription {
		void ddd();
	}

	public interface J extends I {
		@Override
		void ddd();

		void ooo();
	}

	@Priority(1)
	public static abstract class A extends BehaviourComponent implements I {
		@Override
		public void onInput() {
		}
	}

	@Priority(1)
	public static class B extends A implements J {
		@Override
		public void ddd() {
		}

		@Override
		public void ooo() {
		}
	}

	public static class C<T extends A> {
		@SuppressWarnings("unused")
		public void ddd(T a) {

		}
	}

	public static class D extends C<B> {
		@Override
		public void ddd(B a) {
			super.ddd(a);
		}
	}
}
