package com.gurella.engine.resource;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.utils.Array;

public class AssetResourceDescriptor<T> {
	boolean persistent;
	boolean initOnStart;

	AssetResourceType assetResourceType;

	String fileName;
	AssetLoaderParameters<T> parameters;
	final Array<AssetSelector<T>> selectors = new Array<AssetSelector<T>>();

	private AssetDescriptor<T> assetDescriptor;

	AssetResourceDescriptor() {
	}

	public AssetResourceDescriptor(AssetResourceType assetType, String fileName) {
		this.assetResourceType = assetType;
		this.fileName = fileName;
	}

	public AssetDescriptor<T> getAssetDescriptor() {
		if (assetDescriptor == null) {
			assetDescriptor = createAssetDescriptor();
		}
		return assetDescriptor;
	}

	private AssetDescriptor<T> createAssetDescriptor() {
		String resolvedFileName = null;
		AssetLoaderParameters<T> resolvedParameters = null;

		for (int i = 0; i < selectors.size; i++) {
			AssetSelector<T> selector = selectors.get(i);
			if (selector.predicate.evaluate(null)) {
				resolvedFileName = selector.fileName;
				resolvedParameters = selector.parameters;
				break;
			}
		}

		if (resolvedFileName == null) {
			resolvedFileName = fileName;
		}

		if (resolvedParameters == null) {
			resolvedParameters = parameters;
		}

		return new AssetDescriptor<T>(resolvedFileName, getResourceType(), resolvedParameters);
	}

	public String getBaseFileName() {
		return fileName;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getResourceType() {
		return (Class<T>) assetResourceType.resourceType;
	}
}
