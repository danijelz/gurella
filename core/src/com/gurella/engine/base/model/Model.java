package com.gurella.engine.base.model;

import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.ObjectArchive;
import com.gurella.engine.utils.ImmutableArray;

public interface Model<T> {
	Class<T> getType();

	String getName();

	T newInstance(InitializationContext<T> context);

	void initInstance(InitializationContext<T> context);

	ImmutableArray<Property<?>> getProperties();
	
	<P> Property<P> getProperty(String name);
	
	void serialize(T object, Class<?> knownType, ObjectArchive archive);
}
