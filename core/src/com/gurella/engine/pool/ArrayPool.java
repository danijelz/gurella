package com.gurella.engine.pool;

public interface ArrayPool<T> {
	T obtain(int length, int maxLength);
	
	void free(T array);
}
