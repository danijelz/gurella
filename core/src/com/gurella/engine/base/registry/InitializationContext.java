package com.gurella.engine.base.registry;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset.AssetRegistry;
import com.gurella.engine.base.serialization.AssetReference;

public class InitializationContext<T> implements Poolable {
	public ObjectRegistry objectRegistry;
	public AssetRegistry assetRegistry;

	public T initializingObject;
	public T template;
	public Json json;
	public JsonValue serializedValue;
	public boolean duplicate;
	public InitializationContext<?> parentContext;

	private final IntMap<ManagedObject> instances = new IntMap<ManagedObject>();

	public <MO extends ManagedObject> MO getInstance(MO object) {
		return getInstance(object.id);
	}

	public <MO extends ManagedObject> MO getInstance(int objectId) {
		if (parentContext != null) {
			return parentContext.getInstance(objectId);
		}

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

	public boolean fromTemplate() {
		return serializedValue == null && template != null;
	}

	public boolean fromSerializedValue() {
		return serializedValue != null;
	}

	public <A> A getAsset(AssetReference assetReference) {
		return assetRegistry.get(null, true);
	}

	@Override
	public void reset() {
		objectRegistry = null;
		initializingObject = null;
		serializedValue = null;
		template = null;
		duplicate = false;
		parentContext = null;
		instances.clear();
	}
}
