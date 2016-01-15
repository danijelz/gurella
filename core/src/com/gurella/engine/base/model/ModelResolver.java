package com.gurella.engine.base.model;

public interface ModelResolver {
	<T> Model<T> resolve(Class<T> type);
}
