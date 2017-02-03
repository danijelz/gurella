package com.gurella.engine.serialization;

public interface Deserializer {
	<T> T deserialize(Class<T> expectedType);

	<T> T deserialize(Class<T> expectedType, Object template);
}
