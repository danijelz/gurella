package com.gurella.engine.metatype;

import com.gurella.engine.metatype.serialization.Input;
import com.gurella.engine.metatype.serialization.Output;
import com.gurella.engine.utils.Range;

public interface Property<T> {
	String getName();

	Class<T> getType();

	Range<?> getRange(); // TODO remove

	boolean isAsset(); //TODO unused

	boolean isNullable();

	boolean isFinal();

	boolean isCopyable();

	boolean isFlatSerialization();

	boolean isEditable(); // TODO remove

	Property<T> newInstance(MetaType<?> owner);

	T getValue(Object object);

	void setValue(Object object, T value);

	void serialize(Object object, Object template, Output output);

	void deserialize(Object object, Object template, Input input);

	void copy(Object original, Object duplicate, CopyContext context);
}
