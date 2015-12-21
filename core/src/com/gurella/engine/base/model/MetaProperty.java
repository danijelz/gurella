package com.gurella.engine.base.model;

import com.gurella.engine.base.container.InitializationContext;
import com.gurella.engine.utils.Range;

public interface MetaProperty<T> {
	String getName();

	Class<T> getType();

	Range<?> getRange();

	boolean isNullable();

	String getDescriptiveName();

	String getDescription();

	String getGroup();

	void init(InitializationContext<?> context);
}
