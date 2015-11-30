package com.gurella.engine.graph.script;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.utils.ReflectionUtils;

public class ScriptMethodRegistry {
	private static final ObjectMap<Class<?>, ObjectSet<ScriptMethodDescriptor>> markerScriptMethods = new ObjectMap<Class<?>, ObjectSet<ScriptMethodDescriptor>>();
	private static final ObjectMap<Class<?>, ObjectSet<ScriptMethodDescriptor>> scriptMethods = new ObjectMap<Class<?>, ObjectSet<ScriptMethodDescriptor>>();

	private ScriptMethodRegistry() {
	}

	public static synchronized ObjectSet<ScriptMethodDescriptor> getScriptMethods(
			Class<? extends ScriptComponent> componentClass) {
		ObjectSet<ScriptMethodDescriptor> methods = scriptMethods.get(componentClass);
		if (methods != null) {
			return methods;
		}

		if (!ClassReflection.isAssignableFrom(ScriptComponent.class, componentClass)) {
			throw new GdxRuntimeException("Provided class must be subtype of ScriptComponent!");
		}

		initScriptMethods(componentClass);
		return scriptMethods.get(componentClass);
	}

	private static void initScriptMethods(Class<?> componentClass) {
		if (componentClass == SceneNodeComponent.class || scriptMethods.containsKey(componentClass)) {
			return;
		}

		Class<?> superclass = componentClass.getSuperclass();
		initScriptMethods(superclass);

		ObjectSet<ScriptMethodDescriptor> superMethods = scriptMethods.get(superclass);
		ObjectSet<ScriptMethodDescriptor> methods = new ObjectSet<ScriptMethodDescriptor>(superMethods);

		ObjectSet<ScriptMethodDescriptor> superMarkerMethods = markerScriptMethods.get(superclass);
		ObjectSet<ScriptMethodDescriptor> markerMethods = new ObjectSet<ScriptMethodDescriptor>(superMarkerMethods);

		for (Method method : ClassReflection.getDeclaredMethods(componentClass)) {
			ScriptMethodDescriptor descriptor = find(superMarkerMethods, method);
			if (descriptor == null) {
				descriptor = find(superMethods, method);
				if (descriptor == null) {
					ScriptMethod scriptMethod = ReflectionUtils.getDeclaredAnnotation(method, ScriptMethod.class);
					if (scriptMethod != null) {
						descriptor = new ScriptMethodDescriptor(method);
						if (scriptMethod.marker()) {
							markerMethods.add(descriptor);
						} else {
							methods.add(descriptor);
						}
					}
				}
			} else {
				methods.add(descriptor);
				markerMethods.remove(descriptor);
			}
		}

		scriptMethods.put(componentClass, methods);
		markerScriptMethods.put(componentClass, markerMethods);
	}

	private static ScriptMethodDescriptor find(ObjectSet<ScriptMethodDescriptor> methods, Method method) {
		for (ScriptMethodDescriptor descriptor : methods) {
			if (descriptor.isEqual(method)) {
				return descriptor;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		for (Method method : ClassReflection.getMethods(A.class)) {
			System.out.println(method.getName() + ": " + method.getDeclaringClass());
		}

		for (Method method : ClassReflection.getMethods(B.class)) {
			System.out.println(method.getName() + ": " + method.getDeclaringClass());
		}
	}

	public interface I {
		boolean ggg();
	}

	public static abstract class A implements I {
		public void ddd() {
		}
	}

	public static class B extends A {
		@Override
		public void ddd() {
		}

		@Override
		public boolean ggg() {
			return false;
		}
	}
}
