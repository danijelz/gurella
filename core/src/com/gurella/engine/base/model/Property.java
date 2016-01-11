package com.gurella.engine.base.model;

import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.ObjectArchive;
import com.gurella.engine.utils.Range;

public interface Property<T> {
	String getName();

	Class<T> getType();

	Range<?> getRange();

	boolean isNullable();

	String getDescriptiveName();

	String getDescription();

	String getGroup();

	void init(InitializationContext<?> context);

	T getValue(Object object);

	void setValue(Object object, T value);
	
	void serialize(T object, ObjectArchive archive);
}
