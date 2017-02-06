package com.gurella.engine.asset.loader;

import com.badlogic.gdx.files.FileHandle;

public interface AssetLoader<ASYNC, TYPE, PROPS extends AssetProperties> {
	Class<PROPS> getAssetPropertiesType();

	ASYNC init(DependencyCollector collector, FileHandle assetFile);

	ASYNC processAsync(DependencyProvider provider, FileHandle file, ASYNC asyncData, PROPS properties);

	TYPE finish(DependencyProvider provider, FileHandle file, ASYNC asyncData, PROPS properties);
}
