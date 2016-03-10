package com.gurella.engine.base.serialization;

//TODO unused
public interface Serializable<T> {
	void serialize(T instance, Object template, Output output);

	T deserialize(Object template, Input input);
}
