package com.gurella.engine.base.model;

import com.gurella.engine.base.container.InitializationContext;
import com.gurella.engine.utils.ReflectionUtils;

public abstract class AbstractMetaModel<T> implements Model<T> {
	public final Class<T> type;

	public AbstractMetaModel(Class<T> type) {
		this.type = type;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public T createInstance(InitializationContext<T> context) {
		return ReflectionUtils.newInstance(type);
	}
}
