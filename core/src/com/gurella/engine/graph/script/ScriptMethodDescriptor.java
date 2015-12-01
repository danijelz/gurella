package com.gurella.engine.graph.script;

import java.util.Arrays;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.utils.ReflectionUtils;

public final class ScriptMethodDescriptor {
	private static int INDEX = 0;
	static ObjectMap<MethodSignature, ScriptMethodDescriptor> instances = new ObjectMap<MethodSignature, ScriptMethodDescriptor>();

	public final int id;
	public final Class<?> declaringClass;
	public final String name;
	public final Class<?>[] parameterTypes;
	final ScriptMethodDecorator decorator;

	static ScriptMethodDescriptor find(Class<?> declaringClass, String name, Class<?>... parameterTypes) {
		MethodSignature methodSignature = MethodSignature.obtain(declaringClass, name, parameterTypes);
		ScriptMethodDescriptor descriptor = instances.get(methodSignature);
		methodSignature.free();
		return descriptor;
	}

	ScriptMethodDescriptor(Method method, Class<? extends ScriptMethodDecorator> decoratorClass) {
		id = INDEX++;
		this.declaringClass = method.getDeclaringClass();
		this.name = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.decorator = decoratorClass == null || ScriptMethodDecorator.class == decoratorClass ? null
				: ReflectionUtils.newInstance(decoratorClass);
		instances.put(MethodSignature.obtain(declaringClass, name, parameterTypes), this);
	}

	void componentActivated(ScriptComponent component) {
		if (decorator != null) {
			decorator.componentActivated(component);
		}
	}

	void componentDeactivated(ScriptComponent component) {
		if (decorator != null) {
			decorator.componentDeactivated(component);
		}
	}

	boolean isEqual(Method method) {
		return name.equals(method.getName()) && Arrays.equals(parameterTypes, method.getParameterTypes())
				&& ClassReflection.isAssignableFrom(declaringClass, method.getDeclaringClass());
	}

	@Override
	public String toString() {
		return "ScriptMethodDescriptor [declaringClass=" + declaringClass + ", name=" + name + ", parameterTypes="
				+ Arrays.toString(parameterTypes) + "]";
	}
}
