package com.gurella.engine.serialization;

//TODO Input#readReference and Output#writeReference
public interface Reference {
	String getFileName();

	Class<?> getType();
}
