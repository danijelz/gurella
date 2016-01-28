package com.gurella.engine.base.metamodel;

public interface ModelFactory {
	<T> Metamodel<T> create(Class<T> type);
}
