package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.InitializationContext;
import com.gurella.engine.resource.DependencyMap;
import com.gurella.engine.utils.Range;

public interface ModelProperty<T> {
	String getName();

	Class<T> getType();

	Range<?> getRange();

	boolean isNullable();

	String getDescriptiveName();

	String getDescription();

	String getGroup();
	
	T getDefaultValue();

	void initFromDefaultValue(Object resource);

	void initFromSerializableValue(Object resource, Object serializableValue, DependencyMap dependencies);

	void writeValue(Json json, Object serializableValue);

	T readValue(Json json, JsonValue propertyValue);

	void init(InitializationContext context);
}
