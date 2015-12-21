package com.gurella.engine.base.container;

import com.badlogic.gdx.utils.JsonValue;

public class InitializationContext<T> {
	public T initializingObject;
	public JsonValue serializedValue;
	public T template;
}
