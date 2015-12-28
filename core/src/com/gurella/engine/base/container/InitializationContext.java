package com.gurella.engine.base.container;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonValue;

public class InitializationContext<T> {
	public ObjectManager container;
	public T initializingObject;
	public JsonValue serializedValue;
	public T template;

	private IntMap<ManagedObject> instances = new IntMap<ManagedObject>();
	
	public <MO extends ManagedObject> MO getManagedObject(MO object) {
		int instanceId = object.instanceId;
		@SuppressWarnings("unchecked")
		MO instance = (MO) instances.get(instanceId);
		if(instance == null) {
			instance = Objects.duplicate(object);
			instances.put(instanceId, instance);
		}
		return instance;
	}
}
