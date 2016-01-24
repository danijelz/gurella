package com.gurella.engine.base.model;

import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;

public interface Model<T> {
	Class<T> getType();

	String getName();

	T createInstance(InitializationContext context);

	void initInstance(InitializationContext context);

	ImmutableArray<Property<?>> getProperties();

	<P> Property<P> getProperty(String name);

	void serialize(T value, Output output);

	T deserialize(Input input);

	T copy(T original, CopyContext context);
	
	T create(CreationContext context);
}
