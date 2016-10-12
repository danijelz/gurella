package com.gurella.engine.base.model;

import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.Range;

public interface Property<T> {
	String getName();

	Class<T> getType();

	Range<?> getRange(); // TODO remove

	boolean isNullable();

	boolean isFinal();

	boolean isCopyable();

	boolean isFlatSerialization();

	boolean isEditable(); // TODO remove

	Property<T> newInstance(Model<?> model);

	T getValue(Object object);

	void setValue(Object object, T value);

	void serialize(Object object, Object template, Output output);

	void deserialize(Object object, Object template, Input input);

	void copy(Object original, Object duplicate, CopyContext context);
}
