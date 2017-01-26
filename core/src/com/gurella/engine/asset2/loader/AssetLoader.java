package com.gurella.engine.asset2.loader;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.properties.AssetProperties;

public interface AssetLoader<A, T, P extends AssetProperties<T>> {
	A loadAsyncData(DependencyProvider provider, FileHandle file, P properties);

	T consumeAsyncData(DependencyProvider provider, FileHandle file, P properties, A asyncData);

	void injectDependencies(DependencyCollector collector, FileHandle file, AssetProperties<T> properties);
}
