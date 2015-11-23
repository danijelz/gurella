package com.gurella.engine.graph.script;

public interface ScriptMethod {
	Class<?> getMethodDeclaringClass();

	String getMethodName();

	Class<?>[] getMethodParameterTypes();
}
