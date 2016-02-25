package com.gurella.engine.event;

import static com.badlogic.gdx.utils.reflect.ClassReflection.getDeclaredMethods;
import static com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom;
import static com.gurella.engine.utils.Reflection.getAnnotation;
import static com.gurella.engine.utils.Reflection.getDeclaredAnnotation;

import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.event.Priority;

public final class EventSubscriptions {
	private static final ObjectMap<Class<?>, ObjectSet<Class<?>>> subscriptions = new ObjectMap<Class<?>, ObjectSet<Class<?>>>();
	private static final ObjectMap<Class<?>, IntIntMap> priorities = new ObjectMap<Class<?>, IntIntMap>();

	private static final Object lock = new Object();

	static {
		subscriptions.put(Object.class, new ObjectSet<Class<?>>());
		subscriptions.put(EventSubscription.class, new ObjectSet<Class<?>>());
		priorities.put(Object.class, new IntIntMap());
	}

	private EventSubscriptions() {
	}

	public static ObjectSet<Class<?>> getSubscriptions(Class<?> listenerType) {
		synchronized (lock) {
			ObjectSet<Class<?>> impementedSubscribers = subscriptions.get(listenerType);
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

		ObjectSet<Class<?>> listenerSubscriptions = new ObjectSet<Class<?>>();

		Class<?>[] interfaces = ClassReflection.getInterfaces(listenerType);
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> listenerInterface = interfaces[i];
			initCaches(listenerInterface);
			if (isAssignableFrom(EventSubscription.class, listenerInterface)) {
				listenerSubscriptions.add(listenerInterface);
			}
		}

		IntIntMap prioritiesByCallback = new IntIntMap();
		Class<?> superclass = listenerType.getSuperclass();
		if (superclass != null) {
			initCaches(superclass);
			listenerSubscriptions.addAll(subscriptions.get(superclass));
			prioritiesByCallback.putAll(priorities.get(superclass));
		}

		Priority classPriority = getAnnotation(listenerType, Priority.class);
		int priority = getPriority(classPriority, superclass);
		boolean eventSubscriber = listenerType.isInterface() && isAssignableFrom(EventSubscription.class, listenerType);

		for (Method method : getDeclaredMethods(listenerType)) {
			Priority methodPriority = getDeclaredAnnotation(method, Priority.class);
			prioritiesByCallback.put(callbackId, priority);
		}

		subscriptions.put(listenerType, listenerSubscriptions);
		priorities.put(listenerType, prioritiesByCallback);
	}

	private static int getPriority(Priority classPriority, Class<?> superclass) {
		if (classPriority != null) {
			return classPriority.value();
		}

		if (superclass == null) {
			return Integer.MAX_VALUE;
		}

		return getPriority(superclass);
	}

	public static int getPriority(Class<?> listenerType) {
		IntIntMap prioritiesByListnerType = priorities.get(listenerType);
		return prioritiesByListnerType == null ? Integer.MAX_VALUE
				: prioritiesByListnerType.get(callbackId, Integer.MAX_VALUE);
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

	public static abstract class A extends BehaviourComponent implements I {
		@Override
		@Priority(1)
		public void onInput() {
		}
	}

	public static class B extends A implements J {
		@Override
		@Priority(1)
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
