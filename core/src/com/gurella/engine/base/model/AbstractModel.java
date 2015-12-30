package com.gurella.engine.base.model;

import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;

public abstract class AbstractModel<T> implements Model<T> {
	public final Class<T> type;

	public AbstractModel(Class<T> type) {
		this.type = type;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public T createInstance() {
		return ReflectionUtils.newInstance(type);
	}

	@Override
	public void initInstance(InitializationContext<T> context) {
		ImmutableArray<Property<?>> properties = getProperties();
		for (int i = 0; i < properties.size(); i++) {
			properties.get(i).init(context);
		}
	}
}
