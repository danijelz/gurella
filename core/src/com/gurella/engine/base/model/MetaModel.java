package com.gurella.engine.base.model;

import com.gurella.engine.base.container.InitializationContext;
import com.gurella.engine.utils.ImmutableArray;

public interface MetaModel<T> {
	Class<T> getType();

	String getDescriptiveName();

	T createInstance(InitializationContext<T> context);

	ImmutableArray<MetaProperty<?>> getProperties();
}
