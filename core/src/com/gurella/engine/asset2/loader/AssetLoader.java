package com.gurella.engine.asset2.loader;

import com.badlogic.gdx.files.FileHandle;

public interface AssetLoader<ASYNC, TYPE, PROPS extends AssetProperties> {
	Class<PROPS> getAssetPropertiesType();

	void initDependencies(DependencyCollector collector, FileHandle assetFile);

	ASYNC loadAsyncData(DependencyProvider provider, FileHandle file, PROPS properties);

	TYPE consumeAsyncData(DependencyProvider provider, FileHandle file, PROPS properties, ASYNC asyncData);
}
