package com.gurella.engine.graph.event;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.utils.ReflectionUtils;

class EventCallbackRegistry {
	private static final ObjectMap<Class<?>, ObjectSet<EventCallbackInstance<?>>> markerCallbacks = new ObjectMap<Class<?>, ObjectSet<EventCallbackInstance<?>>>();
	private static final ObjectMap<Class<?>, ObjectSet<EventCallbackInstance<?>>> callbacks = new ObjectMap<Class<?>, ObjectSet<EventCallbackInstance<?>>>();

	static {
		markerCallbacks.put(SceneNodeComponent.class, new ObjectSet<EventCallbackInstance<?>>());
		callbacks.put(SceneNodeComponent.class, new ObjectSet<EventCallbackInstance<?>>());
	}

	private EventCallbackRegistry() {
	}

	static synchronized ObjectSet<EventCallbackInstance<?>> getCallbacks(Class<?> componentClass) {
		ObjectSet<EventCallbackInstance<?>> methods = callbacks.get(componentClass);
		if (methods != null) {
			return methods;
		}

		initCallbacks(componentClass);
		return callbacks.get(componentClass);
	}

	static void initCallbacks(Class<?> componentClass) {
		if (componentClass == SceneNodeComponent.class || callbacks.containsKey(componentClass)) {
			return;
		}

		ObjectSet<EventCallbackInstance<?>> methods = new ObjectSet<EventCallbackInstance<?>>();
		ObjectSet<EventCallbackInstance<?>> markerMethods = new ObjectSet<EventCallbackInstance<?>>();

		// TODO replace with ClassReflection.getInterfaces(componentClass)
		Class<?>[] interfaces = componentClass.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> componentInterface = interfaces[i];
			initCallbacks(componentInterface);

			ObjectSet<EventCallbackInstance<?>> interfaceMarkerMethods = markerCallbacks.get(componentInterface);
			if (componentClass.isInterface()) {
				markerMethods.addAll(interfaceMarkerMethods);
			} else {
				for (EventCallbackInstance<?> interfaceMarkerMethod : interfaceMarkerMethods) {
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
			EventCallbackInstance<?> descriptor = find(markerMethods, method);

			if (descriptor == null) {
				descriptor = find(methods, method);
				if (descriptor == null) {
					EventCallback callbackAnnotation = ReflectionUtils.getDeclaredAnnotation(method,
							EventCallback.class);
					if (callbackAnnotation != null) {
						descriptor = new EventCallbackInstance<SceneNodeComponent>(method, callbackAnnotation);
						if (callbackAnnotation.marker() || componentClass.isInterface()) {
							markerMethods.add(descriptor);
						} else {
							methods.add(descriptor);
						}
					}
				}
			} else if (!componentClass.isInterface()) {
				methods.add(descriptor);
				markerMethods.remove(descriptor);
			}
		}

		callbacks.put(componentClass, methods);
		markerCallbacks.put(componentClass, markerMethods);
	}

	private static EventCallbackInstance<?> find(ObjectSet<EventCallbackInstance<?>> callbacks, Method method) {
		if (callbacks != null) {
			for (EventCallbackInstance<?> callback : callbacks) {
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
}
