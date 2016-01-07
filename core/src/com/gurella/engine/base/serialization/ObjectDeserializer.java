package com.gurella.engine.base.serialization;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.AssetRegistry;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.registry.AsyncCallback;
import com.gurella.engine.base.registry.ManagedObject;
import com.gurella.engine.base.registry.ObjectRegistry;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;

public class ObjectDeserializer<T> {
	Json json;
	JsonValue serializedObject;

	Class<T> knownType;
	Class<T> resolvedType;

	T prefab;
	T object;

	ObjectRegistry objectRegistry;
	AssetRegistry assetRegistry;
	Array<ReferenceProperty<?>> referenceProperties = new Array<ReferenceProperty<?>>();
	AsyncCallback<T> callback;

	public void deserialize() {
		resolvedType = resolveObjectType();
		Model<T> model = Models.getModel(resolvedType);
		deserializedObject = model.createInstance();
		if(deserializedObject instanceof ManagedObject) {
			objectRegistry.addObject((ManagedObject) deserializedObject);
		}

		resolvePrefab();

		ImmutableArray<Property<?>> properties = model.getProperties();
		for (int i = 0; i < properties.size(); i++) {
			Property<?> property = properties.get(i);
			JsonValue serializedPropertyValue = serializedObject.get(property.getName());
			deserializeProperty(property, serializedPropertyValue);
		}
	}

	private <V> void deserializeProperty(Property<V> property, JsonValue serializedPropertyValue) {
		if (serializedPropertyValue != null) {

		} else if (prefab != null) {
			property.setValue(deserializedObject, property.getValue(prefab));
		} else if (waitingForPrefab) {

		}
		// TODO Auto-generated method stub
	}

	private Class<T> resolveObjectType() {
		String objectTypeName = serializedObject.getString("class");
		if (objectTypeName != null) {
			return ReflectionUtils.forName(objectTypeName);
		} else if (knownType != null) {
			return knownType;
		} else {
			throw new GdxRuntimeException("Can't resolve object type.");
		}
	}

	private void resolvePrefab() {
		if (!ClassReflection.isAssignableFrom(ManagedObject.class, resolvedType)) {
			return;
		}

		JsonValue prefabValue = serializedObject.get("prefab");
		if (prefabValue == null) {
			return;
		}

		ObjectReference prefabReference = json.readValue(ObjectReference.class, prefabValue);
		if (objectRegistry.isLoaded(prefabReference.getFilePath())) {
			prefab = objectRegistry.getPrefab(prefabReference);
		} else {
			waitingForPrefab = true;
		}
	}
}
