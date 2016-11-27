package com.gurella.engine.serialization;

public interface Serializable<T> {
	void serialize(T instance, Object template, Output output);

	void deserialize(Object template, Input input);
}
