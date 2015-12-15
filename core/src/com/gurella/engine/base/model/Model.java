package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.resource.DependencyMap;

public interface Model<T> {
	Class<T> getType();

	String getDescriptiveName();

	T createInstance(ObjectMap<String, Object> propertyValues, DependencyMap dependencies);

	void init(T resource, ObjectMap<String, Object> propertyValues, DependencyMap dependencies);

	Array<ModelProperty<?>> getProperties();
}
