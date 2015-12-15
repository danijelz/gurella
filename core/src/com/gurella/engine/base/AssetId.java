package com.gurella.engine.base;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.utils.ReflectionUtils;

public class AssetId implements Serializable {
	private static final String FILE_NAME_TAG = "fileName";

	private String fileName;
	private String assetTypeName;

	private Class<?> assetType;

	AssetId() {
	}

	public AssetId(String fileName, String assetTypeName) {
		this.fileName = fileName;
		this.assetTypeName = assetTypeName;
	}

	public AssetId(String fileName, Class<?> assetType) {
		this.fileName = fileName;
		this.assetType = assetType;
		this.assetTypeName = assetType.getName();
	}

	public String getFileName() {
		return fileName;
	}

	public String getAssetTypeName() {
		return assetTypeName;
	}

	public Class<?> getAssetType() {
		if (assetType == null) {
			assetType = ReflectionUtils.forName(assetTypeName);
		}
		return assetType;
	}

	@Override
	public void write(Json json) {
		json.writeField(FILE_NAME_TAG, assetTypeName + " " + fileName);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		String value = jsonData.getString(FILE_NAME_TAG);
		int index = value.indexOf(' ');
		assetTypeName = value.substring(0, index);
		fileName = value.substring(index + 1);
	}
}
