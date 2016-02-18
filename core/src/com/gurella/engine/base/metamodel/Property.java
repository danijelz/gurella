package com.gurella.engine.base.metamodel;

import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.Range;

public interface Property<T> {
	String getName();

	Class<T> getType();

	Model<?> getModel();

	Range<?> getRange();

	boolean isNullable();

	String getDescriptiveName();

	String getDescription();

	String getGroup();

	Property<T> newInstance(Model<?> model);

	T getValue(Object object);

	void setValue(Object object, T value);

	void serialize(Object object, Object template, Output output);

	void deserialize(Object object, Object template, Input input);

	void copy(Object original, Object duplicate, CopyContext context);
}
