package com.gurella.engine.graph.script;

import java.util.Arrays;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.graph.script.ScriptMethodDecorator.NopScriptMethodDecorator;
import com.gurella.engine.utils.ReflectionUtils;

public final class ScriptMethodDescriptor<T> {
	private static int INDEX = 0;
	static ObjectMap<MethodSignature, ScriptMethodDescriptor<?>> instances = new ObjectMap<MethodSignature, ScriptMethodDescriptor<?>>();

	public final int id;
	public final Class<T> declaringClass;
	public final String name;
	public final Class<?>[] parameterTypes;
	final ScriptMethodDecorator decorator;

	static <T> ScriptMethodDescriptor<T> get(Class<T> declaringClass, String name, Class<?>... parameterTypes) {
		ScriptMethodRegistry.checkInitScriptMethods(declaringClass);
		MethodSignature methodSignature = MethodSignature.obtain(declaringClass, name, parameterTypes);
		@SuppressWarnings("unchecked")
		ScriptMethodDescriptor<T> descriptor = (ScriptMethodDescriptor<T>) instances.get(methodSignature);
		methodSignature.free();
		if (descriptor == null) {
			throw new GdxRuntimeException("Can't find method: [declaringClass=" + declaringClass + ", name=" + name
					+ ", parameterTypes=" + Arrays.toString(parameterTypes) + "]");
		}
		return descriptor;
	}

	ScriptMethodDescriptor(Method method, Class<? extends ScriptMethodDecorator> decoratorClass) {
		id = INDEX++;
		@SuppressWarnings("unchecked")
		Class<T> casted = method.getDeclaringClass();
		this.declaringClass = casted;
		this.name = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.decorator = decoratorClass == null || ScriptMethodDecorator.class == decoratorClass
				? NopScriptMethodDecorator.instance : ReflectionUtils.newInstance(decoratorClass);
		instances.put(MethodSignature.obtain(declaringClass, name, parameterTypes), this);
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
