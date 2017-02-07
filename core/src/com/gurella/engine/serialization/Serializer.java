package com.gurella.engine.serialization;

import com.gurella.engine.asset.persister.DependencyLocator;

public interface Serializer {
	<T> void serialize(DependencyLocator locator, Class<T> expectedType, T rootObject, Object template);
}
