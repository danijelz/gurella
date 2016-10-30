package com.gurella.engine.asset.properties;

import com.badlogic.gdx.assets.AssetLoaderParameters;

public interface AssetProperties<T> {
	Class<T> getAssetType();

	AssetLoaderParameters<T> createLoaderParameters();
}
