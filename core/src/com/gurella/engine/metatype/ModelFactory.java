package com.gurella.engine.metatype;

public interface ModelFactory {
	<T> Model<T> create(Class<T> type);
}
