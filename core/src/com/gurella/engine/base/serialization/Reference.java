package com.gurella.engine.base.serialization;

//TODO Input#readReference and Output#writeRefwrwnce
public interface Reference {
	String getFileName();

	Class<?> getValueType();
}
