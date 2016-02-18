package com.gurella.engine.base.metamodel;

public interface ModelFactory {
	<T> Model<T> create(Class<T> type);
}
