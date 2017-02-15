package com.gurella.engine.serialization;

public interface Serializer<SERIALIZED> {
	<T> SERIALIZED serialize(Class<T> expectedType, T rootObject, Object template);
}
