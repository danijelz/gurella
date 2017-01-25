package com.gurella.engine.utils.adapter;

import com.gurella.engine.utils.ImmutableArray;

public interface AdapterFactory {
	ImmutableArray<Class<?>> getAdapterList();

	<T> T getAdapter(Object adaptableObject, Class<T> adapterType);
}
