package com.gurella.engine.graph.script;

import com.gurella.engine.utils.IndexedValue;

public final class ScriptMethod {
	private static final IndexedValue<ScriptMethod> INDEXER = new IndexedValue<ScriptMethod>();

	public final int id;
	public final Class<?> declaringClass;
	public final String name;
	public final Class<?>[] parameterTypes;

	public ScriptMethod(Class<?> declaringClass, String name, Class<?>[] parameterTypes) {
		id = INDEXER.getIndex(this);
		this.declaringClass = declaringClass;
		this.name = name;
		this.parameterTypes = parameterTypes;
	}
}
