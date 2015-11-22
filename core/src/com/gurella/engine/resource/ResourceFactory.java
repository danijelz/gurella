package com.gurella.engine.resource;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;

public interface ResourceFactory<T> extends Disposable, Json.Serializable {
	Class<T> getResourceType();

	T create(ResourceMap dependencies);

	void init(T resource, ResourceMap dependencies);

	IntArray getDependentResourceIds(ResourceContext context);
}