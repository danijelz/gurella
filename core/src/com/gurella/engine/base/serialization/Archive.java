package com.gurella.engine.base.serialization;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.registry.AsyncCallback;
import com.gurella.engine.base.registry.ManagedObject;

public class Archive {
	AsyncCallback<?> callback;
	FileHandle fileHandle;
	
	Class<?> knownType;
	
	Json json;
	JsonValue serializedValue;
	
	private IntMap<ManagedObject> archivedObjects = new IntMap<ManagedObject>();
	private ObjectMap<String, ExtenalDependency> externalDependencies = new ObjectMap<String, ExtenalDependency>();
	
	public void deserialize() {
		
	}
	
	private static class ExtenalDependency {
		Archive archive;
		FileHandle fileHandle;
	}
	
	private static class ObjectDeserializer {
		JsonValue serializedObject;
		Model<?> model;
		Array<PropertyDeserializer> properties = new Array<PropertyDeserializer>();

		void appendReferences(Array<Reference> references) {

		}

		void init() {

		}

		Object deserialize() {
			Object instance = model.newInstance();
		}
	}

	private static class PropertyDeserializer {
		JsonValue serializedProperty;
		Property<?> property;

		ObjectDeserializer object;

		void appendReferences(Array<Reference> references) {
		}

		void init() {
			property.getType();
		}

		void deserialize() {

		}
	}
}
