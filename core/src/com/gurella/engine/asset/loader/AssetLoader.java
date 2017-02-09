package com.gurella.engine.asset.loader;

import com.badlogic.gdx.files.FileHandle;

public interface AssetLoader<ASYNC, TYPE, PROPS extends AssetProperties> {
	Class<PROPS> getPropertiesType();

	ASYNC init(DependencyCollector collector, FileHandle assetFile);

	ASYNC processAsync(DependencySupplier supplier, FileHandle assetFile, ASYNC asyncData, PROPS properties);

	TYPE finish(DependencySupplier supplier, FileHandle assetFile, ASYNC asyncData, PROPS properties);
}
