package com.gurella.engine.serialization;

public interface Serializer<SERIALIZED> {
	<T> SERIALIZED serialize(Class<? super T> expectedType, T rootObject, Object template);
}
