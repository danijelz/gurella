package com.gurella.engine.resource.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.resource.ResourceMap;

public abstract class AbstractResourceModel<T> implements ResourceModel<T> {
	public final Class<T> resourceType;

	public AbstractResourceModel(Class<T> resourceType) {
		this.resourceType = resourceType;
	}

	@Override
	public Class<T> getResourceType() {
		return resourceType;
	}

	@Override
	public T createResource(ObjectMap<String, Object> propertyValues, ResourceMap dependencies) {
		T resource = createResourceInstance(propertyValues);
		initResource(resource, propertyValues, dependencies);
		return resource;
	}

	protected abstract T createResourceInstance(ObjectMap<String, Object> propertyValues);

	@Override
	public void initResource(T resource, ObjectMap<String, Object> propertyValues, ResourceMap dependencies) {
		if (resource == null) {
			return;
		}

		Array<ResourceModelProperty> properties = getProperties();
		for (int i = 0; i < properties.size; i++) {
			ResourceModelProperty property = properties.get(i);
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
