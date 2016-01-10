package com.gurella.engine.base.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.registry.AsyncCallback;
import com.gurella.engine.base.registry.ManagedObject;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.SynchronizedPools;

public class ObjectArchive<T> {
	AsyncCallback<Object> callback;
	String fileName;
	Class<T> knownType;

	Json json;
	JsonValue serializedObject;

	private T rootObject;
	private Array<AssetReference> dependentAssets = new Array<AssetReference>();
	private Array<ObjectReference> dependentObjects = new Array<ObjectReference>();
	private IntMap<ManagedObject> objects = new IntMap<ManagedObject>();
	private Array<ManagedObject> slicedObjects = new Array<ManagedObject>();

	public void deserialize() {
		JsonReader reader = SynchronizedPools.obtain(JsonReader.class);
		serializedObject = reader.parse(Gdx.files.internal(fileName));
		SynchronizedPools.free(reader);
		callback.onProgress(0.01f);
		resolveDependencies();
	}

	public void serialize(T rootObject) {
		this.rootObject = rootObject;
		slice(rootObject);
	}

	private <O> void slice(O obj) {
		Model<O> model = Models.getModel(obj);

		if (model.getType().isArray()) {
			int length = ArrayReflection.getLength(obj);
			for (int i = 0; i < length; i++) {
				Object item = ArrayReflection.get(obj, i);
				slice(item);
			}
		} else {
			ImmutableArray<Property<?>> properties = model.getProperties();
			for (int i = 0; i < properties.size(); i++) {
				Property<?> property = properties.get(i);
				Object value = property.getValue(obj);
				slice(property, value);
			}
		}

		if (slicedObjects.size > 0) {
			slice(slicedObjects.removeIndex(0));
		}
	}

	private <O> void slice(Property<?> property, O value) {
		if (value == null) {
			return;
		} else if (value instanceof ManagedObject) {
			ManagedObject managedObject = (ManagedObject) value;
			String objectFileName = getObjectArchiveFileName(managedObject);
			if (fileName.equals(objectFileName)) {
				slicedObjects.add(managedObject);
			} else {
				ObjectReference reference = new ObjectReference(managedObject.getId(), objectFileName);
				dependentObjects.add(reference);
			}
		} else if (ResourceService.isResource(value)) {
			AssetReference reference = new AssetReference(fileName, value.getClass());
			dependentAssets.add(reference);
		} else if (!Serialization.isSimpleType(property.getType())) {
			slice(value);
		}
	}
}
