package com.gurella.engine.base.model;

import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.ObjectArchive;
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

	Property<T> copy(Model<?> model);

	void init(InitializationContext<?> context);

	T getValue(Object object);

	void setValue(Object object, T value);

	T getDefaultValue();

	void serialize(Object object, ObjectArchive archive);
}
