package com.gurella.engine.asset2.loader;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.properties.AssetProperties;

public interface AssetLoader<ASYNC, TYPE, PROPS extends AssetProperties<TYPE>> {
	ASYNC deserializeAsync(DependencyProvider provider, FileHandle file, PROPS properties);

	TYPE consumeAsyncData(DependencyProvider provider, FileHandle file, PROPS properties, ASYNC asyncData);

	void initDependencies(DependencyCollector collector, FileHandle assetFile);
}
