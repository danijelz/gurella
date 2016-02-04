package com.gurella.engine.asset;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.utils.Array;

public class ConfigurableAssetDescriptor<T> {
	boolean sticky;
	AssetType assetType;

	String fileName;
	AssetLoaderParameters<T> parameters;

	final Array<AssetSelector<T>> selectors = new Array<AssetSelector<T>>();

	boolean resolved;
	String resolvedFileName;
	AssetLoaderParameters<T> resolvedParameters;

	void resolve() {
		if (resolved) {
			return;
		}

		resolved = true;
		for (int i = 0; i < selectors.size; i++) {
			AssetSelector<T> selector = selectors.get(i);
			if (selector.predicate.evaluate(null)) {
				resolvedFileName = selector.fileName;
				resolvedParameters = selector.parameters;
				return;
			}
		}

		resolvedFileName = fileName;
		resolvedParameters = parameters;
	}

	public String getFileName() {
		return fileName;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getAssetType() {
		return (Class<T>) assetType.assetType;
	}
}
