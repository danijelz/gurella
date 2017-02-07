package com.gurella.engine.asset.loader;

import com.badlogic.gdx.files.FileHandle;

public interface AssetLoader<ASYNC, TYPE, PROPS extends AssetProperties> {
	Class<PROPS> getAssetPropertiesType();

	ASYNC init(DependencyCollector collector, FileHandle assetFile);

	ASYNC processAsync(DependencySupplier provider, FileHandle file, ASYNC asyncData, PROPS properties);

	TYPE finish(DependencySupplier provider, FileHandle file, ASYNC asyncData, PROPS properties);
}
