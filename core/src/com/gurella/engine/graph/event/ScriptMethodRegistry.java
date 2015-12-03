package com.gurella.engine.graph.event;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.behaviour.ScriptComponent;
import com.gurella.engine.utils.ReflectionUtils;

class ScriptMethodRegistry {
	private static final ObjectMap<Class<?>, ObjectSet<ScriptMethodDescriptor<?>>> markerScriptMethods = new ObjectMap<Class<?>, ObjectSet<ScriptMethodDescriptor<?>>>();
	private static final ObjectMap<Class<?>, ObjectSet<ScriptMethodDescriptor<?>>> scriptMethods = new ObjectMap<Class<?>, ObjectSet<ScriptMethodDescriptor<?>>>();

	static {
		markerScriptMethods.put(SceneNodeComponent.class, new ObjectSet<ScriptMethodDescriptor<?>>());
		scriptMethods.put(SceneNodeComponent.class, new ObjectSet<ScriptMethodDescriptor<?>>());
	}

	private ScriptMethodRegistry() {
	}

	static synchronized ObjectSet<ScriptMethodDescriptor<?>> getScriptMethods(
			Class<? extends SceneNodeComponent> componentClass) {
		ObjectSet<ScriptMethodDescriptor<?>> methods = scriptMethods.get(componentClass);
		if (methods != null) {
			return methods;
		}

		initScriptMethods(componentClass);
		return scriptMethods.get(componentClass);
	}

	static void initScriptMethods(Class<?> componentClass) {
		if (componentClass == SceneNodeComponent.class || scriptMethods.containsKey(componentClass)) {
			return;
		}

		ObjectSet<ScriptMethodDescriptor<?>> methods = new ObjectSet<ScriptMethodDescriptor<?>>();
		ObjectSet<ScriptMethodDescriptor<?>> markerMethods = new ObjectSet<ScriptMethodDescriptor<?>>();

		// TODO replace with ClassReflection.getInterfaces(componentClass)
		Class<?>[] interfaces = componentClass.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> componentInterface = interfaces[i];
			initScriptMethods(componentInterface);

			ObjectSet<ScriptMethodDescriptor<?>> interfaceMarkerMethods = markerScriptMethods.get(componentInterface);
			if (componentClass.isInterface()) {
				markerMethods.addAll(interfaceMarkerMethods);
			} else {
				for (ScriptMethodDescriptor<?> interfaceMarkerMethod : interfaceMarkerMethods) {
					if (!methods.contains(interfaceMarkerMethod)) {
						markerMethods.add(interfaceMarkerMethod);
					}
				}
			}
		}

		Class<?> superclass = componentClass.getSuperclass();
		if (superclass != null) {
			initScriptMethods(superclass);
			methods.addAll(scriptMethods.get(superclass));
			markerMethods.addAll(markerScriptMethods.get(superclass));

		}

		for (Method method : ClassReflection.getDeclaredMethods(componentClass)) {
			ScriptMethodDescriptor<?> descriptor = find(markerMethods, method);

			if (descriptor == null) {
				descriptor = find(methods, method);
				if (descriptor == null) {
					EventCallback scriptMethod = ReflectionUtils.getDeclaredAnnotation(method, EventCallback.class);
					if (scriptMethod != null) {
						descriptor = new ScriptMethodDescriptor<SceneNodeComponent>(method, scriptMethod);
						if (scriptMethod.marker() || componentClass.isInterface()) {
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

		scriptMethods.put(componentClass, methods);
		markerScriptMethods.put(componentClass, markerMethods);
	}

	private static ScriptMethodDescriptor<?> find(ObjectSet<ScriptMethodDescriptor<?>> methods, Method method) {
		if (methods != null) {
			for (ScriptMethodDescriptor<?> descriptor : methods) {
				if (descriptor.isEqual(method)) {
					return descriptor;
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		initScriptMethods(B.class);
		markerScriptMethods.get(ScriptComponent.class).iterator().toArray();
		markerScriptMethods.get(A.class).iterator().toArray();
		markerScriptMethods.get(B.class).iterator().toArray();
		scriptMethods.get(A.class).iterator().toArray();
		scriptMethods.get(B.class).iterator().toArray();
		markerScriptMethods.get(I.class).iterator().toArray();
		markerScriptMethods.get(J.class).iterator().toArray();
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

	public static abstract class A extends ScriptComponent implements I {
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
