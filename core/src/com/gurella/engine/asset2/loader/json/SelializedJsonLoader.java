package com.gurella.engine.asset2.loader.json;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.loader.DependencyCollector;
import com.gurella.engine.asset2.loader.DependencyProvider;
import com.gurella.engine.serialization.json.JsonInput;

public class SelializedJsonLoader<T> implements AssetLoader<T, T, SelializedJsonProperties> {
	private final Class<T> expectedType;
	private final JsonInput input = new JsonInput();

	public SelializedJsonLoader(Class<T> expectedType) {
		this.expectedType = expectedType;
	}

	@Override
	public T loadAsyncData(DependencyProvider provider, FileHandle file, SelializedJsonProperties properties) {
		return input.deserialize(expectedType);
	}

	@Override
	public T consumeAsyncData(DependencyProvider provider, FileHandle file, SelializedJsonProperties properties,
			T asyncData) {
		return asyncData;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		input.init(assetFile, collector);
	}

	@Override
	public Class<SelializedJsonProperties> getAssetPropertiesType() {
		return SelializedJsonProperties.class;
	}
}
