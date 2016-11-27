package com.gurella.engine.serialization;

//TODO Input#readReference and Output#writeRefwrwnce
public interface Reference {
	String getFileName();

	Class<?> getValueType();
}
