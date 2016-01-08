package com.gurella.engine.base.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.asset.AssetRegistry;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.registry.AsyncCallback;
import com.gurella.engine.base.registry.ManagedObject;
import com.gurella.engine.base.registry.ObjectRegistry;
import com.gurella.engine.utils.ReflectionUtils;

public class LocalObjectsDeserializer {
	AsyncCallback<?> callback;
	FileHandle fileHandle;
	boolean prefab;

	Json json;
	JsonValue serializedObjects;

	ObjectRegistry objectRegistry;
	AssetRegistry assetRegistry;

	LocalObjectsDeserializer parent;
	ObjectMap<String, LocalObjectsDeserializer> children = new ObjectMap<String, LocalObjectsDeserializer>();

	Array<ReferenceProperty<?>> referenceProperties = new Array<ReferenceProperty<?>>();

	LoadingStage stage = LoadingStage.start;

	public void load() {
		prepare();
		initObjects();
	}

	private void prepare() {
		if (serializedObjects == null) {
			serializedObjects = new JsonReader().parse(fileHandle);
		}

		stage = LoadingStage.jsonLoaded;
		notifyProgress();

		if (serializedObjects.isArray()) {
			for (JsonValue serializedObject = serializedObjects.child; serializedObject != null; serializedObject = serializedObject.next) {
				createEmptyObject(serializedObject);
			}
		} else if (serializedObjects.isObject()) {
			createEmptyObject(serializedObjects);
		} else {
			throw new GdxRuntimeException("Invalid json.");
		}

		preparePrefabs();
		initObjects();
	}

	private void createEmptyObject(JsonValue serializedObject) {
		Model<ManagedObject> model = getModel(serializedObject);
		ManagedObject object = model.newInstance();
		object.id = serializedObject.getInt("id");
		object.name = serializedObject.getString("name");

		objectRegistry.addObject(object);

		JsonValue prefabValue = serializedObject.get("prefab");
		if (prefabValue == null) {
			return;
		}

		ObjectReference prefabReference = json.readValue(ObjectReference.class, prefabValue);
		String filePath = prefabReference.getFileName();
		if (assetRegistry.isLoaded(filePath) || children.containsKey(filePath)) {
			return;
		}

		LocalObjectsDeserializer child = new LocalObjectsDeserializer();
		child.objectRegistry = objectRegistry;
		child.json = json;
		child.objectRegistry = objectRegistry;
		child.assetRegistry = assetRegistry;
		child.parent = this;
		child.prefab = true;
		child.fileHandle = Gdx.files.internal(filePath);
		children.put(filePath, child);
	}

	private Model<ManagedObject> getModel(JsonValue serializedObject) {
		String objectTypeName = serializedObject.getString("class");
		if (objectTypeName == null) {
			throw new GdxRuntimeException("Can't resolve object type.");
		}

		return Models.getModel(ReflectionUtils.<ManagedObject> forName(objectTypeName));
	}

	private void preparePrefabs() {
		for (LocalObjectsDeserializer child : children.values()) {
			child.prepare();
			notifyProgress();
		}

		stage = LoadingStage.prefabsLoaded;
	}

	private void initObjects() {
		for (LocalObjectsDeserializer child : children.values()) {
			child.initObjects();
		}

		for (JsonValue serializedObject = serializedObjects.child; serializedObject != null; serializedObject = serializedObject.next) {
			initObject(serializedObject);
		}
	}

	private void initObject(JsonValue serializedObject) {
		ManagedObject object = null;
		ManagedObject prefab = null;
		Model<ManagedObject> model = null;

		// TODO Auto-generated method stub
	}

	private float getProgress() {
		switch (stage) {
		case start:
			return 0;
		case jsonLoaded:
			return 0.05f;
		case prefabsLoaded:
			return 0.1f;
		case loadingAssets:
			// TODO
			return 0.1f;
		default:
			break;
		}

		// TODO Auto-generated method stub
		return 0;
	}

	private void notifyProgress() {
		if (parent == null) {
			float progress = getProgress();
			if (progress == 0) {
				return;
			}
			callback.onProgress(progress);
		} else {
			parent.notifyProgress();
		}
	}

	private enum LoadingStage {
		start, jsonLoaded, prefabsLoaded, loadingAssets
	}

	private static class ObjectMapper {
		LocalObjectsDeserializer deserializer;
		JsonValue serializedObject;
		Model<?> model;
		Array<Reference> properties;

		void appendReferences(Array<Reference> references) {

		}

		void init() {

		}

		Object deserialize() {
			Object instance = model.newInstance();
		}
	}

	private static class PropertyMapper {
		LocalObjectsDeserializer deserializer;
		JsonValue serializedProperty;
		Property<?> property;

		ObjectMapper object;

		void appendReferences(Array<Reference> references) {
		}

		void init() {
			property.getType();
		}

		void deserialize() {

		}
	}
}
