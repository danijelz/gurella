package com.gurella.engine.asset.loader;

import com.badlogic.gdx.files.FileHandle;

public interface AssetLoader<TYPE, PROPS extends AssetProperties> {
	Class<PROPS> getPropertiesType();

	void initDependencies(DependencyCollector collector, FileHandle assetFile);

	void processAsync(DependencySupplier supplier, FileHandle assetFile, PROPS properties);

	TYPE finish(DependencySupplier supplier, FileHandle assetFile, PROPS properties);
}
