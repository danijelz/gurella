package com.gurella.engine.utils.adapter;

public interface Adaptable {
	<T> T getAdapter(Class<T> adapter);
}
