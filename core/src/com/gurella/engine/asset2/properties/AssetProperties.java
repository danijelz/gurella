package com.gurella.engine.asset2.properties;

import com.badlogic.gdx.assets.AssetLoaderParameters;

public interface AssetProperties<T> {
	AssetLoaderParameters<T> createLoaderParameters();
}
