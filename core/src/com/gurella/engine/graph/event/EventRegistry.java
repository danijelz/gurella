package com.gurella.engine.graph.event;

import static com.badlogic.gdx.utils.reflect.ClassReflection.getDeclaredMethods;
import static com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom;
import static com.gurella.engine.utils.ReflectionUtils.getAnnotation;
import static com.gurella.engine.utils.ReflectionUtils.getDeclaredAnnotation;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventTrigger.NopEventTrigger;
import com.gurella.engine.utils.ValueUtils;

class EventRegistry {
	private static final ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>> markerCallbacks = new ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>>();
	private static final ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>> callbacks = new ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>>();
	private static final ObjectMap<Class<?>, ObjectSet<Class<?>>> subscriptions = new ObjectMap<Class<?>, ObjectSet<Class<?>>>();
	private static final ObjectMap<Class<?>, IntIntMap> priorities = new ObjectMap<Class<?>, IntIntMap>();

	private static final ObjectMap<String, EventCallbackIdentifier<?>> callbacksById = new ObjectMap<String, EventCallbackIdentifier<?>>();

	static {
		markerCallbacks.put(Object.class, new ObjectSet<EventCallbackIdentifier<?>>());
		callbacks.put(Object.class, new ObjectSet<EventCallbackIdentifier<?>>());
		subscriptions.put(Object.class, new ObjectSet<Class<?>>());
		priorities.put(Object.class, new IntIntMap());
	}

	private EventRegistry() {
	}

	static synchronized ObjectSet<EventCallbackIdentifier<?>> getCallbacks(Class<?> listenerType) {
		ObjectSet<EventCallbackIdentifier<?>> methods = callbacks.get(listenerType);
		if (methods != null) {
			return methods;
		}

		initCallbacks(listenerType);
		return callbacks.get(listenerType);
	}

	static synchronized ObjectSet<Class<?>> getSubscriptions(Class<?> listenerType) {
		ObjectSet<Class<?>> impementedSubscribers = subscriptions.get(listenerType);
		if (impementedSubscribers != null) {
			return impementedSubscribers;
		}

		initCallbacks(listenerType);
		return subscriptions.get(listenerType);
	}

	private static void initCallbacks(Class<?> listenerType) {
		if (listenerType == Object.class || callbacks.containsKey(listenerType)) {
			return;
		}

		ObjectSet<EventCallbackIdentifier<?>> listenerCallbacks = new ObjectSet<EventCallbackIdentifier<?>>();
		ObjectSet<EventCallbackIdentifier<?>> listenerMarkerCallbacks = new ObjectSet<EventCallbackIdentifier<?>>();
		ObjectSet<Class<?>> listenerSubscriptions = new ObjectSet<Class<?>>();

		// TODO replace with ClassReflection.getInterfaces(componentClass)
		Class<?>[] interfaces = listenerType.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> listenerInterface = interfaces[i];
			initCallbacks(listenerInterface);
			if (listenerInterface != EventSubscription.class
					&& isAssignableFrom(EventSubscription.class, listenerInterface)) {
				listenerSubscriptions.add(listenerInterface);
			}

			ObjectSet<EventCallbackIdentifier<?>> interfaceMarkerCallbacks = markerCallbacks.get(listenerInterface);
			if (listenerType.isInterface()) {
				listenerMarkerCallbacks.addAll(interfaceMarkerCallbacks);
			} else {
				for (EventCallbackIdentifier<?> interfaceMarkerCallback : interfaceMarkerCallbacks) {
					if (!listenerCallbacks.contains(interfaceMarkerCallback)) {
						listenerMarkerCallbacks.add(interfaceMarkerCallback);
					}
				}
			}
		}

		IntIntMap prioritiesByCallback = new IntIntMap();
		Class<?> superclass = listenerType.getSuperclass();
		if (superclass != null) {
			initCallbacks(superclass);
			listenerCallbacks.addAll(callbacks.get(superclass));
			listenerMarkerCallbacks.addAll(markerCallbacks.get(superclass));
			listenerSubscriptions.addAll(subscriptions.get(superclass));
			prioritiesByCallback.putAll(priorities.get(superclass));
		}

		Priority classPriority = getAnnotation(listenerType, Priority.class);
		boolean eventSubscriber = listenerType.isInterface() && isAssignableFrom(EventSubscription.class, listenerType);

		for (Method method : getDeclaredMethods(listenerType)) {
			EventCallback callbackAnnotation = getDeclaredAnnotation(method, EventCallback.class);
			EventCallbackIdentifier<?> callback = find(listenerMarkerCallbacks, method);

			if (callback == null) {
				callback = find(listenerCallbacks, method);
				if (callback == null) {
					if (eventSubscriber || callbackAnnotation != null) {
						callback = createCallbackIdentifier(method, callbackAnnotation);
						if (listenerType.isInterface() || (callbackAnnotation != null && callbackAnnotation.marker())) {
							listenerMarkerCallbacks.add(callback);
						} else {
							listenerCallbacks.add(callback);
						}
					}
				}
			} else if (!listenerType.isInterface() && (callbackAnnotation == null || !callbackAnnotation.marker())) {
				listenerCallbacks.add(callback);
				listenerMarkerCallbacks.remove(callback);
			}

			if (callback != null) {
				int callbackId = callback.id;
				Priority methodPriority = getDeclaredAnnotation(method, Priority.class);
				int priority = getPriority(classPriority, methodPriority, superclass, callbackId);
				prioritiesByCallback.put(callbackId, priority);
			}
		}

		callbacks.put(listenerType, listenerCallbacks);
		markerCallbacks.put(listenerType, listenerMarkerCallbacks);
		subscriptions.put(listenerType, listenerSubscriptions);
		priorities.put(listenerType, prioritiesByCallback);
	}

	private static EventCallbackIdentifier<Object> createCallbackIdentifier(Method method,
			EventCallback callbackAnnotation) {
		String id = callbackAnnotation == null ? null : callbackAnnotation.id();
		id = ValueUtils.isEmpty(id) ? method.getName() : id;
		String fullId = method.getDeclaringClass().getName() + id;

		if (callbacksById.containsKey(fullId)) {
			throw new GdxRuntimeException(
					"Duplicate event id: [declaringClass=" + method.getDeclaringClass() + ", id=" + id + "]");
		}

		Class<? extends EventTrigger> triggerClass = callbackAnnotation == null ? NopEventTrigger.class
				: callbackAnnotation.trigger();
		EventCallbackIdentifier<Object> callbackIdentifier = new EventCallbackIdentifier<Object>(method, triggerClass);

		callbacksById.put(fullId, callbackIdentifier);
		return callbackIdentifier;
	}

	private static int getPriority(Priority classPriority, Priority methodPriority,
			Class<?> superclass, int callbackId) {
		if (methodPriority != null) {
			return methodPriority.value();
		}

		if (classPriority != null) {
			return classPriority.value();
		}

		if (superclass == null) {
			return Integer.MAX_VALUE;
		}

		return getPriority(superclass, callbackId);
	}

	static int getPriority(Class<?> listenerType, int callbackId) {
		IntIntMap prioritiesByListnerType = priorities.get(listenerType);
		return prioritiesByListnerType == null ? Integer.MAX_VALUE
				: prioritiesByListnerType.get(callbackId, Integer.MAX_VALUE);
	}

	private static EventCallbackIdentifier<?> find(ObjectSet<EventCallbackIdentifier<?>> callbacks, Method method) {
		if (callbacks != null) {
			for (EventCallbackIdentifier<?> callback : callbacks) {
				if (callback.isEqual(method)) {
					return callback;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	static <T> EventCallbackIdentifier<T> getIdentifier(Class<T> declaringClass, String id) {
		initCallbacks(declaringClass);
		return (EventCallbackIdentifier<T>) callbacksById.get(declaringClass.getName() + id);
	}

	public static void main(String[] args) {
		initCallbacks(B.class);
		markerCallbacks.get(BehaviourComponent.class).iterator().toArray();
		markerCallbacks.get(A.class).iterator().toArray();
		markerCallbacks.get(B.class).iterator().toArray();
		callbacks.get(A.class).iterator().toArray();
		callbacks.get(B.class).iterator().toArray();
		markerCallbacks.get(I.class).iterator().toArray();
		markerCallbacks.get(J.class).iterator().toArray();
		subscriptions.get(I.class).iterator().toArray();
		subscriptions.get(J.class).iterator().toArray();
		subscriptions.get(A.class).iterator().toArray();
		subscriptions.get(B.class).iterator().toArray();
	}

	public interface I extends EventSubscription {
		@EventCallback
		void ddd();
	}

	public interface J extends I {
		@Override
		@EventCallback
		void ddd();

		@EventCallback
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
