package com.gurella.engine.base.model;

public interface ModelFactory {
	<T> Model<T> create(Class<T> type);
}
