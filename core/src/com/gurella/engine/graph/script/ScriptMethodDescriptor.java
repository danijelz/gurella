package com.gurella.engine.graph.script;

import java.util.Arrays;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.utils.ReflectionUtils;

public final class ScriptMethodDescriptor {
	private static int INDEX = 0;

	public final int id;
	public final Class<?> declaringClass;
	public final String name;
	public final Class<?>[] parameterTypes;
	final ScriptMethodDecorator decorator;

	ScriptMethodDescriptor(Method method) {
		id = INDEX++;
		this.declaringClass = method.getDeclaringClass();
		this.name = method.getName();
		this.parameterTypes = method.getParameterTypes();
		ScriptMethodDecoratorProvider annotation = ReflectionUtils.getDeclaredAnnotation(method,
				ScriptMethodDecoratorProvider.class);
		this.decorator = annotation == null ? null : ReflectionUtils.newInstance(annotation.decorator());
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

	boolean isEqual(Class<?> declaringClass, String name, Class<?>[] parameterTypes) {
		return this.name.equals(name) && Arrays.equals(this.parameterTypes, parameterTypes)
				&& ClassReflection.isAssignableFrom(this.declaringClass, declaringClass);
	}

	@Override
	public String toString() {
		return "ScriptMethodDescriptor [declaringClass=" + declaringClass + ", name=" + name + ", parameterTypes="
				+ Arrays.toString(parameterTypes) + "]";
	}
}
