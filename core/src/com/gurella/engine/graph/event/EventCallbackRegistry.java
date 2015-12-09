package com.gurella.engine.graph.event;

import static com.gurella.engine.utils.ReflectionUtils.getAnnotation;
import static com.gurella.engine.utils.ReflectionUtils.getDeclaredAnnotation;

import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.utils.ReflectionUtils;

class EventCallbackRegistry {
	private static final ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>> markerCallbacks = new ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>>();
	private static final ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>> callbacks = new ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>>();
	private static final ObjectMap<Class<?>, IntIntMap> priorities = new ObjectMap<Class<?>, IntIntMap>();

	static {
		markerCallbacks.put(Object.class, new ObjectSet<EventCallbackIdentifier<?>>());
		callbacks.put(Object.class, new ObjectSet<EventCallbackIdentifier<?>>());
	}

	private EventCallbackRegistry() {
	}

	static synchronized ObjectSet<EventCallbackIdentifier<?>> getCallbacks(Class<?> listenerClass) {
		ObjectSet<EventCallbackIdentifier<?>> methods = callbacks.get(listenerClass);
		if (methods != null) {
			return methods;
		}

		initCallbacks(listenerClass);
		return callbacks.get(listenerClass);
	}

	static void initCallbacks(Class<?> listenerClass) {
		if (listenerClass == Object.class || callbacks.containsKey(listenerClass)) {
			return;
		}

		ObjectSet<EventCallbackIdentifier<?>> methods = new ObjectSet<EventCallbackIdentifier<?>>();
		ObjectSet<EventCallbackIdentifier<?>> markerMethods = new ObjectSet<EventCallbackIdentifier<?>>();

		// TODO replace with ClassReflection.getInterfaces(componentClass)
		Class<?>[] interfaces = listenerClass.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> componentInterface = interfaces[i];
			initCallbacks(componentInterface);

			ObjectSet<EventCallbackIdentifier<?>> interfaceMarkerMethods = markerCallbacks.get(componentInterface);
			if (listenerClass.isInterface()) {
				markerMethods.addAll(interfaceMarkerMethods);
			} else {
				for (EventCallbackIdentifier<?> interfaceMarkerMethod : interfaceMarkerMethods) {
					if (!methods.contains(interfaceMarkerMethod)) {
						markerMethods.add(interfaceMarkerMethod);
					}
				}
			}
		}

		Class<?> superclass = listenerClass.getSuperclass();
		if (superclass != null) {
			initCallbacks(superclass);
			methods.addAll(callbacks.get(superclass));
			markerMethods.addAll(markerCallbacks.get(superclass));

		}

		IntIntMap prioritiesByCallback = new IntIntMap();
		EventCallbackPriority classPriority = getAnnotation(listenerClass, EventCallbackPriority.class);

		for (Method method : ClassReflection.getDeclaredMethods(listenerClass)) {
			EventCallback callbackAnnotation = getDeclaredAnnotation(method, EventCallback.class);
			EventCallbackIdentifier<?> callback = find(markerMethods, method);

			if (callback == null) {
				callback = find(methods, method);
				if (callback == null) {
					if (callbackAnnotation != null) {
						callback = new EventCallbackIdentifier<Object>(method, callbackAnnotation);
						if (callbackAnnotation.marker() || listenerClass.isInterface()) {
							markerMethods.add(callback);
						} else {
							methods.add(callback);
						}
					}
				}
			} else if (!listenerClass.isInterface() && (callbackAnnotation == null || !callbackAnnotation.marker())) {
				methods.add(callback);
				markerMethods.remove(callback);
			}

			if (callback != null) {
				int callbackId = callback.id;
				EventCallbackPriority methodPriority = getDeclaredAnnotation(method, EventCallbackPriority.class);
				prioritiesByCallback.put(callbackId,
						getPriority(classPriority, methodPriority, superclass, callbackId));
			}
		}

		callbacks.put(listenerClass, methods);
		markerCallbacks.put(listenerClass, markerMethods);
		priorities.put(listenerClass, prioritiesByCallback);
	}

	private static int getPriority(EventCallbackPriority classPriority, EventCallbackPriority methodPriority,
			Class<?> superclass, int callbackId) {
		if (methodPriority != null) {
			return methodPriority.value();
		}

		if (classPriority != null) {
			return classPriority.value();
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

	public static void main(String[] args) {
		initCallbacks(B.class);
		markerCallbacks.get(BehaviourComponent.class).iterator().toArray();
		markerCallbacks.get(A.class).iterator().toArray();
		markerCallbacks.get(B.class).iterator().toArray();
		callbacks.get(A.class).iterator().toArray();
		callbacks.get(B.class).iterator().toArray();
		markerCallbacks.get(I.class).iterator().toArray();
		markerCallbacks.get(J.class).iterator().toArray();
		ClassReflection.getAnnotations(B.class);
		ReflectionUtils.getMethod(B.class, "ddd").isAnnotationPresent(EventCallback.class);
		ClassReflection.getDeclaredMethods(C.class);
		ClassReflection.getDeclaredMethods(D.class);
	}

	public interface I {
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
		public void onInput() {
		}
	}

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
		@EventCallbackPriority(1)
		public void ddd(B a) {
			super.ddd(a);
		}
	}
}
