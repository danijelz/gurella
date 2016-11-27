package com.gurella.engine.metatype;

import com.gurella.engine.serialization.Input;
import com.gurella.engine.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;

public interface Model<T> {
	Class<T> getType();

	String getName();

	ImmutableArray<Property<?>> getProperties();

	<P> Property<P> getProperty(String name);

	void serialize(T instance, Object template, Output output);

	T deserialize(Object template, Input input);

	T copy(T source, CopyContext context);
}
