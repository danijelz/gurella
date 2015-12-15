package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.AssetId;
import com.gurella.engine.base.ObjectReference;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public class AbstractModelProperty<T> implements ModelProperty<T> {
	private static final String CLASS_TAG = "class";
	private static final String TYPED_ASSET_ID_PREFIX = "@ ";
	private static final String ASSET_ID_PREFIX = "@";
	private static final String RESOURCE_ID_PREFIX = "#";
	
	@Override
	public String getDescriptiveName() {
		return getName();
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getGroup() {
		return "";
	}

	@Override
	public void writeValue(Json json, Object serializableValue) {
		if (!ValueUtils.isEqual(getDefaultValue(), serializableValue)) {
			Class<T> serializableValueType = getSerializableValueType();
			if (ModelResourceFactory.class.equals(serializableValueType)) {
				writeModelResourceFactory(json, serializableValue);
			} else {
				json.writeValue(getName(), serializableValue, serializableValueType);
			}
		}
	}

	private void writeModelResourceFactory(Json json, Object serializableValue) {
		if (serializableValue == null) {
			json.writeValue(getName(), (Object) null);
			return;
		} else if (serializableValue instanceof ObjectReference) {
			json.writeValue(getName(), RESOURCE_ID_PREFIX + Integer.toString(((ObjectReference) serializableValue).getId()));
		} else if (serializableValue instanceof AssetId) {
			AssetId assetId = (AssetId) serializableValue;
			if (getType().equals(assetId.getAssetType())) {
				json.writeValue(getName(), ASSET_ID_PREFIX + assetId.getFileName());
			} else {
				json.writeValue(getName(),
						TYPED_ASSET_ID_PREFIX + assetId.getAssetTypeName() + " " + assetId.getFileName());
			}
		} else {
			ModelResourceFactory<?> factory = (ModelResourceFactory<?>) serializableValue;
			Class<?> propertyType = getType();
			Class<?> resourceType = factory.getResourceType();

			json.writeObjectStart(getName());
			if (!propertyType.equals(resourceType)) {
				json.writeValue(CLASS_TAG, resourceType.getName());
			}

			factory.writeProperties(json);
			json.writeObjectEnd();
		}
	}

	protected abstract Class<T> getSerializableValueType();

	@Override
	public T readValue(Json json, JsonValue propertyValue) {
		Class<T> serializableValueType = getSerializableValueType();
		if (ModelResourceFactory.class.equals(serializableValueType)) {
			return readModelResourceFactory(json, propertyValue);
		} else {
			return json.readValue(serializableValueType, propertyValue);
		}
	}

	private Object readModelResourceFactory(Json json, JsonValue objectValue) {
		if (objectValue.isNull()) {
			return null;
		} else if (objectValue.isString()) {
			String stringValue = objectValue.asString();
			if (stringValue.startsWith(RESOURCE_ID_PREFIX)) {
				return new ObjectReference(Integer.parseInt(stringValue.substring(1)));
			} else if (stringValue.startsWith(TYPED_ASSET_ID_PREFIX)) {
				int index = stringValue.indexOf(' ', 2);
				String assetTypeName = stringValue.substring(2, index);
				String fileName = stringValue.substring(index + 1);
				return new AssetId(fileName, assetTypeName);
			} else if (stringValue.startsWith(ASSET_ID_PREFIX)) {
				String fileName = stringValue.substring(1);
				Class<T> assetType = getType();
				return new AssetId(fileName, assetType);
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			return readFactory(json, objectValue);
		}
	}

	private ModelResourceFactory<?> readFactory(Json json, JsonValue properties) {
		JsonValue classValue = properties.get(CLASS_TAG);
		Class<?> resourceType = classValue == null
				? getPropertyType()
				: ReflectionUtils.forName(classValue.asString());

		@SuppressWarnings("unchecked")
		ModelResourceFactory<Object> factory = new ModelResourceFactory<Object>((Class<Object>) resourceType);
		factory.readProperties(json, properties);
		return factory;
	}
}
