package com.gurella.engine.base.registry;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;

public class InitializationContext<T> implements Poolable {
	public ObjectRegistry registry;
	public T initializingObject;
	public T template;
	public Json json;
	public JsonValue serializedValue;
	public boolean duplicate;
	public InitializationLevel level;
	public InitializationContext<?> parentContext;

	private IntMap<ManagedObject> instances = new IntMap<ManagedObject>();

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

		instance = registry.getObject(objectId);
		if (instance != null) {
			if (duplicate) {
				instance = Objects.duplicate(instance);
			}
			instances.put(objectId, instance);
			return instance;
		}

		MO template = registry.getTemplate(objectId);
		if (template == null) {
			throw new GdxRuntimeException("Can't find object by id: " + objectId);
		}

		instance = Objects.duplicate(template);
		instances.put(objectId, instance);
		return instance;
	}

	@Override
	public void reset() {
		registry = null;
		initializingObject = null;
		serializedValue = null;
		template = null;
		duplicate = false;
		level = null;
		parentContext = null;
		instances.clear();
	}

	public enum InitializationLevel {
		lazy, full;
	}
}
