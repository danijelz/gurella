package com.gurella.engine.base.container;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;

public class InitializationContext<T> implements Poolable {
	public ObjectManager manager;
	public T initializingObject;
	public T template;
	public Json json;
	public JsonValue serializedValue;

	private IntMap<ManagedObject> instances = new IntMap<ManagedObject>();
	
	public <MO extends ManagedObject> MO findManagedObject(int objectId) {
		return manager.get(objectId);
	}

	public <MO extends ManagedObject> MO getManagedObject(MO object) {
		int objectId = object.id;
		@SuppressWarnings("unchecked")
		MO instance = (MO) instances.get(objectId);
		if (instance == null) {
			instance = Objects.duplicate(object);
			instances.put(objectId, instance);
		}
		return instance;
	}

	@Override
	public void reset() {
		manager = null;
		initializingObject = null;
		serializedValue = null;
		template = null;
	}
}
