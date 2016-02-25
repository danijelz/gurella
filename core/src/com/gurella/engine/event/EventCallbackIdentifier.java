package com.gurella.engine.event;

import java.util.Arrays;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;

public final class EventCallbackIdentifier<T> {
	private static int INDEX = 0;

	public final int id;
	public final Class<T> declaringClass;
	public final String name;
	public final Class<?>[] parameterTypes;

	public static <T> EventCallbackIdentifier<T> get(Class<T> declaringClass, String id) {
		EventCallbackIdentifier<T> instance = EventSubscriptions.getIdentifier(declaringClass, id);
		if (instance == null) {
			throw new GdxRuntimeException(
					"Can't find callback: [declaringClass=" + declaringClass + ", id=" + id + "]");
		}
		return instance;
	}

	EventCallbackIdentifier(Method method) {
		id = INDEX++;
		@SuppressWarnings("unchecked")
		Class<T> castedDeclaringClass = method.getDeclaringClass();
		declaringClass = castedDeclaringClass;
		name = method.getName();
		parameterTypes = method.getParameterTypes();
	}

	boolean isEqual(Method method) {
		// TODO generic parameters not handled
		return name.equals(method.getName()) && Arrays.equals(parameterTypes, method.getParameterTypes())
				&& ClassReflection.isAssignableFrom(declaringClass, method.getDeclaringClass());
	}

	@Override
	public String toString() {
		return "EventCallbackInstance [declaringClass=" + declaringClass + ", name=" + name + ", parameterTypes="
				+ Arrays.toString(parameterTypes) + "]";
	}
}
