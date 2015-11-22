package com.gurella.engine.resource.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.resource.ResourceMap;

public interface ResourceModel<T> {
	Class<T> getResourceType();
	
	String getDescriptiveName();

	T createResource(ObjectMap<String, Object> propertyValues, ResourceMap dependencies);

	void initResource(T resource, ObjectMap<String, Object> propertyValues, ResourceMap dependencies);

	Array<ResourceModelProperty> getProperties();
}
