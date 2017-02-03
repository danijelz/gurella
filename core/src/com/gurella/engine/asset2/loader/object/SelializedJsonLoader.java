package com.gurella.engine.asset2.loader.object;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.loader.DependencyCollector;
import com.gurella.engine.asset2.loader.DependencyProvider;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.serialization.json.JsonInput;

public class SelializedJsonLoader<T> implements AssetLoader<T, T, AssetProperties<T>> {
	private final Class<T> expectedType;
	private final JsonInput input = new JsonInput();

	public SelializedJsonLoader(Class<T> expectedType) {
		this.expectedType = expectedType;
	}

	@Override
	public T loadAsync(DependencyProvider provider, FileHandle file, AssetProperties<T> properties) {
		return input.deserialize(expectedType);
	}

	@Override
	public T consumeAsyncData(DependencyProvider provider, FileHandle file, AssetProperties<T> properties,
			T asyncData) {
		return asyncData;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		input.init(assetFile);
		// TODO input.getExternalDependencies()
	}

	@Override
	public Class<AssetProperties<T>> getAssetPropertiesType() {
		return null;
	}
}
