package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;

//TODO unused
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
	public T createInstance(InitializationContext<T> context) {
		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			T template = context.template;
			if (template == null) {
				return null;
			}

			@SuppressWarnings("unchecked")
			T instance = (T) ReflectionUtils.newInstance(template.getClass());
			return instance;
		} else {
			if (serializedValue.isNull()) {
				return null;
			}
			String explicitTypeName = serializedValue.getString("class", null);
			Class<T> resolvedType = explicitTypeName == null ? type : ReflectionUtils.<T> forName(explicitTypeName);
			T instance = (T) ReflectionUtils.newInstance(resolvedType);
			return instance;
		}
	}

	@Override
	public void initInstance(InitializationContext<T> context) {
		if (context.initializingObject == null) {
			return;
		}

		ImmutableArray<Property<?>> properties = getProperties();
		for (int i = 0; i < properties.size(); i++) {
			properties.get(i).init(context);
		}
	}
}
