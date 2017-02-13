package com.gurella.engine.asset.loader.json;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;
import com.gurella.engine.serialization.json.JsonInput;

public class SelializedJsonLoader<T> implements AssetLoader<T, SelializedJsonProperties> {
	private final Class<T> expectedType;
	private final JsonInput input = new JsonInput();
	private T result;

	public SelializedJsonLoader(Class<T> expectedType) {
		this.expectedType = expectedType;
	}

	@Override
	public Class<SelializedJsonProperties> getPropertiesType() {
		return SelializedJsonProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		input.init(assetFile, collector);
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle file, SelializedJsonProperties properties) {
		result = input.deserialize(provider, expectedType, null);
	}

	@Override
	public T finish(DependencySupplier provider, FileHandle file, SelializedJsonProperties properties) {
		T temp = result;
		result = null;
		return temp;
	}
}
