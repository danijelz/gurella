package com.gurella.engine.event2;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.utils.ReflectionUtils;

class EventCallbackRegistry {
	private static final ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>> markerCallbacks = new ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>>();
	private static final ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>> callbacks = new ObjectMap<Class<?>, ObjectSet<EventCallbackIdentifier<?>>>();

	static {
		markerCallbacks.put(Object.class, new ObjectSet<EventCallbackIdentifier<?>>());
		callbacks.put(Object.class, new ObjectSet<EventCallbackIdentifier<?>>());
	}

	private EventCallbackRegistry() {
	}

	static synchronized ObjectSet<EventCallbackIdentifier<?>> getCallbacks(Class<?> componentClass) {
		ObjectSet<EventCallbackIdentifier<?>> methods = callbacks.get(componentClass);
		if (methods != null) {
			return methods;
		}

		initCallbacks(componentClass);
		return callbacks.get(componentClass);
	}

	static void initCallbacks(Class<?> componentClass) {
		if (componentClass == Object.class || callbacks.containsKey(componentClass)) {
			return;
		}

		ObjectSet<EventCallbackIdentifier<?>> methods = new ObjectSet<EventCallbackIdentifier<?>>();
		ObjectSet<EventCallbackIdentifier<?>> markerMethods = new ObjectSet<EventCallbackIdentifier<?>>();

		// TODO replace with ClassReflection.getInterfaces(componentClass)
		Class<?>[] interfaces = componentClass.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> componentInterface = interfaces[i];
			initCallbacks(componentInterface);

			ObjectSet<EventCallbackIdentifier<?>> interfaceMarkerMethods = markerCallbacks.get(componentInterface);
			if (componentClass.isInterface()) {
				markerMethods.addAll(interfaceMarkerMethods);
			} else {
				for (EventCallbackIdentifier<?> interfaceMarkerMethod : interfaceMarkerMethods) {
					if (!methods.contains(interfaceMarkerMethod)) {
						markerMethods.add(interfaceMarkerMethod);
					}
				}
			}
		}

		Class<?> superclass = componentClass.getSuperclass();
		if (superclass != null) {
			initCallbacks(superclass);
			methods.addAll(callbacks.get(superclass));
			markerMethods.addAll(markerCallbacks.get(superclass));

		}

		for (Method method : ClassReflection.getDeclaredMethods(componentClass)) {
			EventCallback callbackAnnotation = ReflectionUtils.getDeclaredAnnotation(method, EventCallback.class);
			EventCallbackIdentifier<?> descriptor = find(markerMethods, method);

			if (descriptor == null) {
				descriptor = find(methods, method);
				if (descriptor == null) {
					if (callbackAnnotation != null) {
						descriptor = new EventCallbackIdentifier<Object>(method, callbackAnnotation);
						if (callbackAnnotation.marker() || componentClass.isInterface()) {
							markerMethods.add(descriptor);
						} else {
							methods.add(descriptor);
						}
					}
				}
			} else if (!componentClass.isInterface() && (callbackAnnotation == null || !callbackAnnotation.marker())) {
				methods.add(descriptor);
				markerMethods.remove(descriptor);
			}
		}

		callbacks.put(componentClass, methods);
		markerCallbacks.put(componentClass, markerMethods);
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
}
