package com.gurella.engine.graph.event;

import java.util.Arrays;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.graph.event.EventCallbackDecorator.NopEventCallbackDecorator;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public final class EventCallbackInstance<T> {
	private static int INDEX = 0;
	private static final ObjectMap<String, EventCallbackInstance<?>> instancesById = new ObjectMap<String, EventCallbackInstance<?>>();

	public final int id;
	public final Class<T> declaringClass;
	public final String name;
	public final Class<?>[] parameterTypes;
	final EventCallbackDecorator decorator;

	public static <T> EventCallbackInstance<T> get(Class<T> declaringClass, String id) {
		EventCallbackRegistry.initCallbacks(declaringClass);
		@SuppressWarnings("unchecked")
		EventCallbackInstance<T> instance = (EventCallbackInstance<T>) instancesById.get(declaringClass.getName() + id);
		if (instance == null) {
			throw new GdxRuntimeException("Can't find callback: [declaringClass=" + declaringClass + ", id=" + id + "]");
		}
		return instance;
	}

	EventCallbackInstance(Method method, EventCallback callbackAnnotation) {
		id = INDEX++;
		@SuppressWarnings("unchecked")
		Class<T> castedDeclaringClass = method.getDeclaringClass();
		declaringClass = castedDeclaringClass;
		name = method.getName();
		parameterTypes = method.getParameterTypes();
		Class<? extends EventCallbackDecorator> decoratorClass = callbackAnnotation.decorator();
		decorator = decoratorClass == null || EventCallbackDecorator.class == decoratorClass
				? NopEventCallbackDecorator.instance : ReflectionUtils.newInstance(decoratorClass);

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
