package com.gurella.engine.resource.factory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.resource.ResourceContext;
import com.gurella.engine.resource.ResourceFactory;
import com.gurella.engine.resource.ResourceMap;
import com.gurella.engine.resource.model.ResourceModel;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.engine.resource.model.ResourceModelUtils;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public final class ModelResourceFactory<T> implements ResourceFactory<T> {
	private Class<T> resourceType;

	private IntArray cachedDependencies;
	private ObjectMap<String, Object> propertyValues = new ObjectMap<String, Object>();

	private ResourceModel<T> model;

	public ModelResourceFactory() {
	}

	public ModelResourceFactory(Class<T> resourceType) {
		this.resourceType = resourceType;
	}

	public ModelResourceFactory(ModelResourceFactory<T> other) {
		resourceType = other.resourceType;
		model = other.model;
		propertyValues.putAll(other.propertyValues);
		cachedDependencies = other.cachedDependencies == null
				? null
				: new IntArray(other.cachedDependencies);
	}

	@Override
	public Class<T> getResourceType() {
		return resourceType;
	}

	@Override
	public T create(ResourceMap dependencies) {
		return getModel().createResource(propertyValues, dependencies);
	}

	@Override
	public void init(T resource, ResourceMap dependencies) {
		getModel().initResource(resource, propertyValues, dependencies);
	}

	public void setPropertyValue(String propertyName, Object properyValue) {
		propertyValues.put(propertyName, properyValue);
	}

	@SuppressWarnings("unchecked")
	public <V> V getPropertyValue(String propertyName) {
		return (V) propertyValues.get(propertyName);
	}

	public <V> V getPropertyValue(String propertyName, V defaultValue) {
		if (containsPropertyValue(propertyName)) {
			return getPropertyValue(propertyName);
		} else {
			return defaultValue;
		}
	}

	public <V> V getPropertyValueOrDefault(String propertyName) {
		if (containsPropertyValue(propertyName)) {
			return getPropertyValue(propertyName);
		} else {
			ResourceModelProperty property = getProperty(propertyName);
			if (property == null) {
				return null;
			} else {
				@SuppressWarnings("unchecked")
				V casted = (V) property.getDefaultValue();
				return casted;
			}
		}
	}

	public boolean containsPropertyValue(String propertyName) {
		return propertyValues.containsKey(propertyName);
	}

	public ResourceModel<T> getModel() {
		if (model == null) {
			model = ResourceModelUtils.getModel(resourceType);
		}
		return model;
	}

	public ResourceModelProperty getProperty(String propertyName) {
		Array<ResourceModelProperty> properties = getModel().getProperties();
		for (int i = 0; i < properties.size; i++) {
			ResourceModelProperty property = properties.get(i);
			if (property.getName().equals(propertyName)) {
				return property;
			}
		}
		return null;
	}

	@Override
	public IntArray getDependentResourceIds(ResourceContext context) {
		if (cachedDependencies == null) {
			cachedDependencies = new IntArray();
			Array<ResourceModelProperty> properties = getModel().getProperties();
			for (int i = 0; i < properties.size; i++) {
				ResourceModelProperty property = properties.get(i);
				String propertyName = property.getName();
				Object serializableValue = propertyValues.get(propertyName);
				if (serializableValue != null) {
					ResourceModelUtils.appendDependentResourceIds(context, serializableValue, cachedDependencies);
				}
			}
		}
		return cachedDependencies;
	}

	@Override
	public void write(Json json) {
		json.writeValue("resourceType", resourceType.getName());

		if (propertyValues.size > 0) {
			json.writeObjectStart("properties");
			writeProperties(json);
			json.writeObjectEnd();
		}
	}

	public void writeProperties(Json json) {
		Array<ResourceModelProperty> properties = getModel().getProperties();
		for (int i = 0; i < properties.size; i++) {
			ResourceModelProperty property = properties.get(i);
			String propertyName = property.getName();
			if (propertyValues.containsKey(propertyName)) {
				Object serializableValue = propertyValues.get(propertyName);
				property.writeValue(json, serializableValue);
			}
		}
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		resourceType = ReflectionUtils.forName(jsonData.getString("resourceType"));

		JsonValue propertiesValuesMap = jsonData.get("properties");
		if (propertiesValuesMap != null) {
			Array<ResourceModelProperty> properties = getModel().getProperties();
			for (int i = 0; i < properties.size; i++) {
				ResourceModelProperty property = properties.get(i);
				String propertyName = property.getName();
				if (propertiesValuesMap.has(propertyName)) {
					propertyValues.put(propertyName, property.readValue(json, propertiesValuesMap.get(propertyName)));
				}
			}
		}
	}

	public void readProperties(Json json, JsonValue propertiesValuesMap) {
		Array<ResourceModelProperty> properties = getModel().getProperties();
		for (int i = 0; i < properties.size; i++) {
			ResourceModelProperty property = properties.get(i);
			String propertyName = property.getName();
			if (propertiesValuesMap.has(propertyName)) {
				propertyValues.put(propertyName, property.readValue(json, propertiesValuesMap.get(propertyName)));
			}
		}
	}

	@Override
	public void dispose() {
		cachedDependencies.clear();
		cachedDependencies = null;
		resourceType = null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (obj.getClass() == getClass()) {
			ModelResourceFactory<?> other = (ModelResourceFactory<?>) obj;
			return resourceType.equals(other.resourceType) && getModel().equals(other.getModel())
					&& areEqualPropertyValues(other);
		} else {
			return false;
		}
	}

	private boolean areEqualPropertyValues(ModelResourceFactory<?> other) {
		Array<ResourceModelProperty> properties = getModel().getProperties();
		ObjectMap<String, Object> otherPropertyValues = other.propertyValues;

		for (int i = 0; i < properties.size; i++) {
			ResourceModelProperty property = properties.get(i);
			String name = property.getName();

			if (propertyValues.containsKey(name)) {
				Object value = propertyValues.get(name);
				Object otherValue = otherPropertyValues.containsKey(name)
						? otherPropertyValues.get(name)
						: property.getDefaultValue();
				if (!ValueUtils.isEqual(value, otherValue)) {
					return false;
				}
			} else if (otherPropertyValues.containsKey(name)) {
				Object value = property.getDefaultValue();
				Object otherValue = otherPropertyValues.get(name);
				if (!ValueUtils.isEqual(value, otherValue)) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		return resourceType.hashCode() + getModel().hashCode() + propertyValuesHashCode();
	}

	private int propertyValuesHashCode() {
		int hash = 31;

		Array<ResourceModelProperty> properties = getModel().getProperties();

		for (int i = 0; i < properties.size; i++) {
			ResourceModelProperty property = properties.get(i);
			String name = property.getName();

			Object value = propertyValues.containsKey(name)
					? propertyValues.get(name)
					: property.getDefaultValue();

			hash += value == null
					? 0
					: value.hashCode();
		}

		return hash;
	}
}
