package com.gurella.engine.base.resource;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.utils.ReflectionUtils;

public class ResourceReference implements Serializable {
	private static final String FILE_NAME_TAG = "fileName";

	private String fileName;
	private String resourceTypeName;

	private Class<?> resourceType;;

	ResourceReference() {
	}

	public ResourceReference(String fileName, String assetTypeName) {
		this.fileName = fileName;
		this.resourceTypeName = assetTypeName;
	}

	public ResourceReference(String fileName, Class<?> assetType) {
		this.fileName = fileName;
		this.resourceType = assetType;
		this.resourceTypeName = assetType.getName();
	}

	public String getFileName() {
		return fileName;
	}

	public String getResourceTypeName() {
		return resourceTypeName;
	}

	public Class<?> getResourceType() {
		if (resourceType == null) {
			resourceType = ReflectionUtils.forName(resourceTypeName);
		}
		return resourceType;
	}

	@Override
	public void write(Json json) {
		json.writeField(FILE_NAME_TAG, resourceTypeName + " " + fileName);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		String value = jsonData.getString(FILE_NAME_TAG);
		int index = value.indexOf(' ');
		resourceTypeName = value.substring(0, index);
		fileName = value.substring(index + 1);
	}
}
