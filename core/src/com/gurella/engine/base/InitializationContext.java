package com.gurella.engine.base;

import com.badlogic.gdx.utils.JsonValue;

public class InitializationContext {
	Container container;
	ManagedObject initializingObject;
	JsonValue serializedValues;
	ManagedObject template;
}
