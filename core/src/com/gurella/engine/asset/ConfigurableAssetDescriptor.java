package com.gurella.engine.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.utils.Array;

public class ConfigurableAssetDescriptor<T> {
	boolean sticky;
	AssetType assetType;
	String fileName;
	final Array<AssetSelector<T>> selectors = new Array<AssetSelector<T>>();

	AssetLoaderParameters<T> parameters;
	AssetDescriptor<T> assetDescriptor;

	ConfigurableAssetDescriptor() {
	}

	public ConfigurableAssetDescriptor(AssetType assetType, String fileName) {
		this.assetType = assetType;
		this.fileName = fileName;
	}

	public AssetDescriptor<T> getAssetDescriptor() {
		if (assetDescriptor == null) {
			assetDescriptor = createAssetDescriptor();
		}
		return assetDescriptor;
	}

	private AssetDescriptor<T> createAssetDescriptor() {
		for (int i = 0; i < selectors.size; i++) {
			AssetSelector<T> selector = selectors.get(i);
			if (selector.predicate.evaluate(null)) {
				return new AssetDescriptor<T>(selector.fileName, getAssetType(), selector.parameters);
			}
		}

		return new AssetDescriptor<T>(fileName, getAssetType(), parameters);
	}

	public String getFileName() {
		return fileName;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getAssetType() {
		return (Class<T>) assetType.assetType;
	}
}
