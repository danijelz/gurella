package com.gurella.engine.metatype;

public interface MetaTypeFactory {
	<T> MetaType<T> create(Class<T> type);
}
