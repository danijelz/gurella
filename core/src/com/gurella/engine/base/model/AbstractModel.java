package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.resource.DependencyMap;

public abstract class AbstractModel<T> implements Model<T>{
	public final Class<T> type;

	public AbstractModel(Class<T> type) {
		this.type = type;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public T createInstance(ObjectMap<String, Object> propertyValues, DependencyMap dependencies) {
		T resource = createInstance(propertyValues);
		init(resource, propertyValues, dependencies);
		return resource;
	}

	protected abstract T createInstance(ObjectMap<String, Object> propertyValues);

	@Override
	public void init(T resource, ObjectMap<String, Object> propertyValues, DependencyMap dependencies) {
		if (resource == null) {
			return;
		}

		Array<ModelProperty<?>> properties = getProperties();
		for (int i = 0; i < properties.size; i++) {
			ModelProperty<?> property = properties.get(i);
			String propertyName = property.getName();
			if (propertyValues.containsKey(propertyName)) {
				Object serializableValue = propertyValues.get(propertyName);
				property.initFromSerializableValue(resource, serializableValue, dependencies);
			} else {
				property.initFromDefaultValue(resource);
			}
		}
	}
}
