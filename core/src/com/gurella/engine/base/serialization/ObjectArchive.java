package com.gurella.engine.base.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.registry.AsyncCallback;
import com.gurella.engine.base.registry.ManagedObject;
import com.gurella.engine.utils.SynchronizedPools;

public class ObjectArchive {
	AsyncCallback<Object> callback;
	String fileName;
	Class<?> knownType;
	
	Json json;
	JsonValue serializedObject;
	
	private int rootObjectId;
	private Array<AssetReference> dependentAssets = new Array<AssetReference>();
	private Array<ObjectReference> dependentObjects = new Array<ObjectReference>();
	private IntMap<ManagedObject> objects = new IntMap<ManagedObject>();
	
	public void deserialize() {
		JsonReader reader = SynchronizedPools.obtain(JsonReader.class);
		serializedObject = reader.parse(Gdx.files.internal(fileName));
		SynchronizedPools.free(reader);
		rootDeserializer = obtainObjectDeserializer(this, knownType, serializedObject);
		callback.onProgress(0.01f);
		resolveDependencies();
	}
	
	public void serialize(Object obj) {
		
	}
}
