package com.gurella.engine.resource;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.utils.Reflection;

public abstract class FactoryResourceReference<T> extends ResourceReference<T> {
	private static final String MODEL_TAG = "model";
	private static final String CLASS_TAG = "class";
	private static final String RESOURCE_FACTORY_TAG = "resourceFactory";

	private ResourceFactory<T> resourceFactory;

	protected FactoryResourceReference() {
	}

	public FactoryResourceReference(int id, String name, boolean persistent, boolean initOnStart,
			ResourceFactory<T> resourceFactory) {
		super(id, name, persistent, initOnStart);
		this.resourceFactory = resourceFactory;
	}

	@Override
	public ResourceFactory<T> getResourceFactory() {
		return resourceFactory;
	}

	@Override
	public void write(Json json) {
		super.write(json);
		if (resourceFactory instanceof ModelResourceFactory) {
			json.writeObjectStart(MODEL_TAG);
			json.writeValue(CLASS_TAG, resourceFactory.getResourceType().getName());
			((ModelResourceFactory<?>) resourceFactory).writeProperties(json);
			json.writeObjectEnd();
		} else {
			json.writeValue(RESOURCE_FACTORY_TAG, resourceFactory, null);
		}
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		JsonValue modelJsonValue = jsonData.get(MODEL_TAG);
		if (modelJsonValue != null) {
			Class<T> resourceType = Reflection.forName(modelJsonValue.getString(CLASS_TAG));
			resourceFactory = new ModelResourceFactory<T>(resourceType);
			((ModelResourceFactory<T>) resourceFactory).readProperties(json, modelJsonValue);
		} else {
			resourceFactory = json.readValue(null, jsonData.get(RESOURCE_FACTORY_TAG));
		}
	}
}
