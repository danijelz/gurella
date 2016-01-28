package com.gurella.engine.base.metamodel;

import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;

public interface Metamodel<T> {
	Class<T> getType();

	String getName();

	ImmutableArray<Property<?>> getProperties();

	<P> Property<P> getProperty(String name);

	void serialize(T instance, Object template, Output output);

	T deserialize(Object template, Input input);

	T copy(T original, CopyContext context);
}
