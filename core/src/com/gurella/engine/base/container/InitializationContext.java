package com.gurella.engine.base.container;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public class InitializationContext<T> {
	public Container container;
	public T initializingObject;
	public JsonValue serializedValue;
	public T template;

	private IntMap<ManagedObject> instances = new IntMap<ManagedObject>();
	private ObjectMap<String, Object> overrides = new ObjectMap<String, Object>();
}
