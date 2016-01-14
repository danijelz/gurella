package com.gurella.engine.base.model;

import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.utils.ImmutableArray;

public interface Model<T> {
	Class<T> getType();

	String getName();

	T createInstance(InitializationContext<T> context);

	void initInstance(InitializationContext<T> context);

	ImmutableArray<Property<?>> getProperties();

	<P> Property<P> getProperty(String name);

	void serialize(T object, Class<?> knownType, Archive archive);
}
