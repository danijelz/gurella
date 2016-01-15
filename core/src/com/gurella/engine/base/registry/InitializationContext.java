package com.gurella.engine.base.registry;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset.AssetRegistry;
import com.gurella.engine.base.serialization.AssetReference;

public class InitializationContext implements Poolable {
	public ObjectRegistry objectRegistry;
	public AssetRegistry assetRegistry;

	public Json json = new Json();
	public boolean duplicate;

	private Array<Object> initializingObjectStack = new Array<Object>();
	private Array<Object> templateStack = new Array<Object>();
	private Array<JsonValue> serializedValueStack = new Array<JsonValue>();

	private final IntMap<ManagedObject> instances = new IntMap<ManagedObject>();

	public void push(Object initializingObject, Object template, JsonValue serializedValue) {
		initializingObjectStack.add(initializingObject);
		templateStack.add(template);
		serializedValueStack.add(serializedValue);
	}

	public void pop() {
		initializingObjectStack.pop();
		templateStack.pop();
		serializedValueStack.pop();
	}

	public <MO extends ManagedObject> MO getInstance(MO object) {
		return getInstance(object.id);
	}

	public <MO extends ManagedObject> MO getInstance(int objectId) {
		@SuppressWarnings("unchecked")
		MO instance = (MO) instances.get(objectId);
		if (instance != null) {
			return instance;
		}

		instance = objectRegistry.getObject(objectId);
		if (instance != null) {
			if (duplicate) {
				instance = Objects.duplicate(instance);
			}
			instances.put(objectId, instance);
			return instance;
		}

		MO template = objectRegistry.getTemplate(objectId);
		if (template == null) {
			throw new GdxRuntimeException("Can't find object by id: " + objectId);
		}

		instance = Objects.duplicate(template, this);
		instances.put(objectId, instance);
		return instance;
	}

	public <A> A getAsset(AssetReference assetReference) {
		return assetRegistry.get(null, true);
	}

	@Override
	public void reset() {
		objectRegistry = null;
		duplicate = false;
		instances.clear();
		initializingObjectStack.clear();
		templateStack.clear();
		serializedValueStack.clear();
	}

	@SuppressWarnings("unchecked")
	public <T> T initializingObject() {
		return (T) initializingObjectStack.peek();
	}

	public void setInitializingObject(Object obj) {
		initializingObjectStack.set(initializingObjectStack.size - 1, obj);
	}

	@SuppressWarnings("unchecked")
	public <T> T template() {
		return (T) templateStack.peek();
	}

	public JsonValue serializedValue() {
		return serializedValueStack.peek();
	}
}
