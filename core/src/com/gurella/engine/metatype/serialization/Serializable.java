package com.gurella.engine.metatype.serialization;

public interface Serializable<T> {
	void serialize(T instance, Object template, Output output);

	void deserialize(Object template, Input input);
}
