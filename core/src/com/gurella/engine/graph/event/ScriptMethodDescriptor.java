package com.gurella.engine.graph.event;

import java.util.Arrays;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.graph.event.ScriptMethodDecorator.NopScriptMethodDecorator;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public final class ScriptMethodDescriptor<T> {
	private static int INDEX = 0;
	private static final ObjectMap<MethodSignature, ScriptMethodDescriptor<?>> instances = new ObjectMap<MethodSignature, ScriptMethodDescriptor<?>>();
	private static final ObjectMap<String, ScriptMethodDescriptor<?>> instancesById = new ObjectMap<String, ScriptMethodDescriptor<?>>();

	public final int id;
	public final Class<T> declaringClass;
	public final String name;
	public final Class<?>[] parameterTypes;
	final ScriptMethodDecorator decorator;

	static <T> ScriptMethodDescriptor<T> get(Class<T> declaringClass, String id) {
		ScriptMethodRegistry.initScriptMethods(declaringClass);
		@SuppressWarnings("unchecked")
		ScriptMethodDescriptor<T> descriptor = (ScriptMethodDescriptor<T>) instancesById
				.get(declaringClass.getName() + id);
		if (descriptor == null) {
			throw new GdxRuntimeException("Can't find method: [declaringClass=" + declaringClass + ", id=" + id + "]");
		}
		return descriptor;
	}

	ScriptMethodDescriptor(Method method, EventCallback scriptMethod) {
		id = INDEX++;
		@SuppressWarnings("unchecked")
		Class<T> casted = method.getDeclaringClass();
		this.declaringClass = casted;
		this.name = method.getName();
		this.parameterTypes = method.getParameterTypes();
		Class<? extends ScriptMethodDecorator> decoratorClass = scriptMethod.decorator();
		this.decorator = decoratorClass == null || ScriptMethodDecorator.class == decoratorClass
				? NopScriptMethodDecorator.instance : ReflectionUtils.newInstance(decoratorClass);

		instances.put(MethodSignature.obtain(declaringClass, name, parameterTypes), this);

		String id = scriptMethod.id();
		id = ValueUtils.isEmpty(id) ? method.getName() : id;
		String fullId = method.getDeclaringClass().getName() + id;
		if (instancesById.containsKey(fullId)) {
			throw new GdxRuntimeException("Duplicate event id: [declaringClass=" + declaringClass + ", id=" + id + "]");
		}
		instancesById.put(fullId, this);
	}

	boolean isEqual(Method method) {
		//TODO generic parameters not handled
		return name.equals(method.getName()) && Arrays.equals(parameterTypes, method.getParameterTypes())
				&& ClassReflection.isAssignableFrom(declaringClass, method.getDeclaringClass());
	}

	@Override
	public String toString() {
		return "ScriptMethodDescriptor [declaringClass=" + declaringClass + ", name=" + name + ", parameterTypes="
				+ Arrays.toString(parameterTypes) + "]";
	}
}
