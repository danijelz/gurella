package com.gurella.engine.base.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.AssetRegistry;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.registry.AsyncCallback;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.ManagedObject;
import com.gurella.engine.base.registry.ObjectRegistry;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.SynchronizedPools;
import com.gurella.engine.utils.ValueUtils;

public class Archive implements Poolable {
	AsyncCallback<Object> callback;
	String fileName;
	Class<?> knownType;

	Json json;
	JsonValue serializedObject;
	ObjectDeserializer<?> rootDeserializer;
	InitializationContext<?> initializationContext = new InitializationContext<Object>();

	ObjectRegistry objectRegistry;
	AssetRegistry assetRegistry;

	private ObjectMap<String, ExtenalDependency<?>> externalDependencies = new ObjectMap<String, ExtenalDependency<?>>();
	private Array<Throwable> exceptions = new Array<Throwable>();

	public void deserialize() {
		JsonReader reader = SynchronizedPools.obtain(JsonReader.class);
		serializedObject = reader.parse(Gdx.files.internal(fileName));
		SynchronizedPools.free(reader);
		rootDeserializer = obtainObjectDeserializer(this, knownType, serializedObject);
		callback.onProgress(0.01f);
		resolveDependencies();
	}

	private void updateProgress() {
		float dependenciesProgress = 0;
		int dependenciesSize = externalDependencies.size;
		for (ExtenalDependency<?> extenalDependency : externalDependencies.values()) {
			dependenciesProgress += extenalDependency.progress;
		}

		boolean finished = dependenciesProgress == dependenciesSize;
		dependenciesProgress *= 0.99f;
		dependenciesProgress /= dependenciesSize;

		float progress = dependenciesProgress + 0.01f;
		callback.onProgress(progress);

		if (finished) {
			if (exceptions.size == 0) {
				callback.onSuccess(value);
			} else {
				callback.onException(exceptions);
			}
		}
	}

	private void addException(Throwable exception) {
		exceptions.add(exception);
		updateProgress();
	}

	private void resolveDependencies() {
		if (externalDependencies.size == 0) {
			rootDeserializer.deserialize(this);
			callback.onProgress(1);
			callback.onSuccess(rootDeserializer.deserialize(this));
		}

		for (ExtenalDependency<?> extenalDependency : externalDependencies.values()) {
			extenalDependency.resolve(this);
		}
	}

	private static <T> ObjectDeserializer<T> obtainObjectDeserializer(Archive archive, Class<?> knownType,
			JsonValue serializedObject) {
		@SuppressWarnings("unchecked")
		ObjectDeserializer<T> deserializer = SynchronizedPools.obtain(ObjectDeserializer.class);
		@SuppressWarnings("unchecked")
		Class<T> casted = (Class<T>) knownType;
		deserializer.knownType = casted;
		deserializer.serializedObject = serializedObject;
		deserializer.init(archive);
		return deserializer;
	}

	private static <T> PropertyDeserializer<T> obtainPropertyDeserializer(Archive archive, Property<T> property,
			JsonValue serializedObject) {
		@SuppressWarnings("unchecked")
		PropertyDeserializer<T> deserializer = SynchronizedPools.obtain(PropertyDeserializer.class);
		deserializer.property = property;
		deserializer.serializedProperty = serializedObject.get(property.getName());
		deserializer.init(archive);
		return deserializer;
	}

	private static class ExtenalDependency<T> implements AsyncCallback<T>, Poolable {
		String fileName;
		Class<T> knownType;
		Archive archive;
		private float progress;

		public void resolve(Archive archive) {
			ResourceService.loadResource(fileName, knownType, this);
		}

		@Override
		public void onSuccess(T value) {
			// TODO value
			progress = 1;
			archive.updateProgress();
		}

		@Override
		public void onException(Throwable exception) {
			progress = 1;
			archive.addException(exception);
		}

		@Override
		public void onProgress(float progress) {
			this.progress = progress;
			archive.updateProgress();
		}

		@Override
		public void reset() {
			fileName = null;
			knownType = null;
			archive = null;
			progress = 0;
		}
	}

	private static class ObjectDeserializer<T> implements Poolable {
		Class<T> knownType;
		Class<T> resolvedType;
		JsonValue serializedObject;

		Model<T> model;
		Array<PropertyDeserializer<?>> properties = new Array<PropertyDeserializer<?>>();

		void init(Archive archive) {
			resolvedType = Serialization.resolveObjectType(knownType, serializedObject);
			model = Models.getModel(resolvedType);

			JsonValue templateRef = serializedObject.get("templateRef");
			if (templateRef != null) {
				ObjectReference reference = archive.json.readValue(ObjectReference.class, templateRef);
				String fileName = reference.getFileName();
				if (ValueUtils.isNotEmpty(fileName) && !archive.externalDependencies.containsKey(fileName)) {
					@SuppressWarnings("unchecked")
					ExtenalDependency<T> dependency = SynchronizedPools.obtain(ExtenalDependency.class);
					dependency.fileName = fileName;
					dependency.knownType = knownType;
					archive.externalDependencies.put(fileName, dependency);
				}
			}

			ImmutableArray<Property<?>> modelProperties = model.getProperties();
			for (int i = 0; i < modelProperties.size(); i++) {
				Property<?> property = modelProperties.get(i);
				properties.add(obtainPropertyDeserializer(archive, property, serializedObject));
			}
		}

		Object deserialize(Archive archive) {

		}

		@Override
		public void reset() {
			knownType = null;
			resolvedType = null;
			serializedObject = null;

			model = null;
			for (int i = 0; i < properties.size; i++) {
				SynchronizedPools.free(properties.get(i));
			}
			properties.clear();
		}
	}

	private static class PropertyDeserializer<T> implements Poolable {
		JsonValue serializedProperty;
		Property<T> property;

		ObjectDeserializer<T> object;

		void init(Archive archive) {
			Class<T> knownType = property.getType();
			if (Assets.isAssetType(knownType)) {
				AssetReference reference = archive.json.readValue(AssetReference.class, serializedProperty);
				String fileName = reference.getFileName();
				if (!archive.externalDependencies.containsKey(fileName)) {
					@SuppressWarnings("unchecked")
					ExtenalDependency<T> dependency = SynchronizedPools.obtain(ExtenalDependency.class);
					dependency.fileName = fileName;
					dependency.knownType = knownType;
					archive.externalDependencies.put(fileName, dependency);
				}
			} else if (ClassReflection.isAssignableFrom(ManagedObject.class, knownType)) {
				ObjectReference reference = archive.json.readValue(ObjectReference.class, serializedProperty);
				String fileName = reference.getFileName();
				if (ValueUtils.isNotEmpty(fileName) && !archive.externalDependencies.containsKey(fileName)) {
					@SuppressWarnings("unchecked")
					ExtenalDependency<T> dependency = SynchronizedPools.obtain(ExtenalDependency.class);
					dependency.fileName = fileName;
					dependency.knownType = knownType;
					archive.externalDependencies.put(fileName, dependency);
				}
			} else if (serializedProperty.isObject() || serializedProperty.isArray()) {
				object = obtainObjectDeserializer(archive, knownType, serializedProperty);
			}
		}

		void deserialize(Archive archive) {

		}

		@Override
		public void reset() {
			serializedProperty = null;
			property = null;
			if (object != null) {
				SynchronizedPools.free(object);
				object = null;
			}
		}
	}
}
