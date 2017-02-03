package com.gurella.engine.serialization;

public interface Serializer {
	<T> void serialize(Class<T> expectedType, T rootObject);

	<T> void serialize(Class<T> expectedType, T rootObject, Object template);
}
