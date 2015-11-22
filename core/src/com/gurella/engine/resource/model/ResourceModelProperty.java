package com.gurella.engine.resource.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.resource.ResourceMap;
import com.gurella.engine.utils.Range;

public interface ResourceModelProperty {
	String getName();

	Class<?> getPropertyType();

	Range<?> getRange();

	boolean isNullable();

	String getDescriptiveName();

	String getDescription();

	String getGroup();
	
	Object getDefaultValue();

	void initFromDefaultValue(Object resource);

	void initFromSerializableValue(Object resource, Object serializableValue, ResourceMap dependencies);

	void writeValue(Json json, Object serializableValue);

	Object readValue(Json json, JsonValue propertyValue);
}
