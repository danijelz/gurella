package com.gurella.engine.asset.loader.json;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencyProvider;
import com.gurella.engine.serialization.json.JsonInput;

public class SelializedJsonLoader<T> implements AssetLoader<T, T, SelializedJsonProperties> {
	private final Class<T> expectedType;
	private final JsonInput input = new JsonInput();

	public SelializedJsonLoader(Class<T> expectedType) {
		this.expectedType = expectedType;
	}

	@Override
	public Class<SelializedJsonProperties> getAssetPropertiesType() {
		return SelializedJsonProperties.class;
	}

	@Override
	public T init(DependencyCollector collector, FileHandle assetFile) {
		input.init(assetFile, collector);
		return null;
	}

	@Override
	public T processAsync(DependencyProvider provider, FileHandle file, T asyncData,
			SelializedJsonProperties properties) {
		return input.deserialize(expectedType, null);
	}

	@Override
	public T finish(DependencyProvider provider, FileHandle file, T asyncData,
			SelializedJsonProperties properties) {
		return asyncData;
	}
}
