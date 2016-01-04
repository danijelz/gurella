package com.gurella.engine.base.model;

import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.utils.ImmutableArray;

public interface Model<T> {
	Class<T> getType();

	String getName();

	T createInstance();

	void initInstance(InitializationContext<T> context);

	ImmutableArray<Property<?>> getProperties();
	
	<P> Property<P> getProperty(String name);
}
