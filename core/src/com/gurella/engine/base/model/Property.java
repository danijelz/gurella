package com.gurella.engine.base.model;

import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.Range;

public interface Property<T> {
	String getName();

	Class<T> getType();

	Range<?> getRange();

	boolean isNullable();
	
	boolean isFinal();
	
	boolean isCopyable();
	
	boolean isFlat();
	
	String getDescriptiveName();

	String getDescription();

	String getGroup();
	
	boolean isEditable();

	Property<T> newInstance(Model<?> model);

	T getValue(Object object);

	void setValue(Object object, T value);

	void serialize(Object object, Object template, Output output);

	void deserialize(Object object, Object template, Input input);

	void copy(Object original, Object duplicate, CopyContext context);
}
