package com.gurella.engine.base.serialization.json;

import com.gurella.engine.utils.ReflectionUtils;

public class AssetReference {
	private String fileName;
	private String assetTypeName;

	private Class<?> assetType;

	AssetReference() {
	}

	public AssetReference(String fileName, String assetTypeName) {
		this.fileName = fileName;
		this.assetTypeName = assetTypeName;
	}

	public AssetReference(String fileName, Class<?> assetType) {
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
}
