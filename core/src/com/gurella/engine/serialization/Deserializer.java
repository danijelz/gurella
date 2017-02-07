package com.gurella.engine.serialization;

import com.gurella.engine.asset.loader.DependencySupplier;

public interface Deserializer {
	<T> T deserialize(DependencySupplier supplier, Class<T> expectedType, Object template);
}
