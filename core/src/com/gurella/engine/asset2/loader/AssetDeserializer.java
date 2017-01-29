package com.gurella.engine.asset2.loader;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.properties.AssetProperties;

public interface AssetDeserializer<A, T, P extends AssetProperties<T>> {
	A deserializeAsync(DependencyProvider provider, FileHandle file, P properties);

	T consumeAsyncData(DependencyProvider provider, FileHandle file, P properties, A asyncData);

	void injectDependencies(DependencyCollector collector, FileHandle assetFile);
}
