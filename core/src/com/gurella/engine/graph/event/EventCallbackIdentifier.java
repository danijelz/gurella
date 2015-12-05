package com.gurella.engine.graph.event;

import java.util.Arrays;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.utils.ValueUtils;

public final class EventCallbackIdentifier<T> {
	private static int INDEX = 0;
	private static final ObjectMap<String, EventCallbackIdentifier<?>> instancesById = new ObjectMap<String, EventCallbackIdentifier<?>>();

	public final int id;
	public final Class<T> declaringClass;
	public final String name;
	public final Class<?>[] parameterTypes;
	final Class<? extends EventTrigger> triggerClass;

	public static <T> EventCallbackIdentifier<T> get(Class<T> declaringClass, String id) {
		EventCallbackRegistry.initCallbacks(declaringClass);
		@SuppressWarnings("unchecked")
		EventCallbackIdentifier<T> instance = (EventCallbackIdentifier<T>) instancesById
				.get(declaringClass.getName() + id);
		if (instance == null) {
			throw new GdxRuntimeException(
					"Can't find callback: [declaringClass=" + declaringClass + ", id=" + id + "]");
		}
		return instance;
	}

	EventCallbackIdentifier(Method method, EventCallback callbackAnnotation) {
		id = INDEX++;
		@SuppressWarnings("unchecked")
		Class<T> castedDeclaringClass = method.getDeclaringClass();
		declaringClass = castedDeclaringClass;
		name = method.getName();
		parameterTypes = method.getParameterTypes();
		triggerClass = callbackAnnotation.trigger();

		String id = callbackAnnotation.id();
		id = ValueUtils.isEmpty(id) ? method.getName() : id;
		String fullId = method.getDeclaringClass().getName() + id;
		if (instancesById.containsKey(fullId)) {
			throw new GdxRuntimeException("Duplicate event id: [declaringClass=" + declaringClass + ", id=" + id + "]");
		}

		instancesById.put(fullId, this);
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
