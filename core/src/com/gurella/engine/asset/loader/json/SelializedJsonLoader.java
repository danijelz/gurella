package com.gurella.engine.asset.loader.json;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;
import com.gurella.engine.metatype.serialization.json.JsonInput;

public class SelializedJsonLoader<T> extends BaseAssetLoader<T, SelializedJsonProperties> {
	private final Class<T> expectedType;
	private final JsonInput input = new JsonInput();

	public SelializedJsonLoader(Class<T> expectedType) {
		this.expectedType = expectedType;
	}

	@Override
	public Class<SelializedJsonProperties> getPropertiesType() {
		return SelializedJsonProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		//input.reset();
		input.init(assetFile, collector);
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle assetFile, SelializedJsonProperties properties) {
		put(assetFile, input.deserialize(provider, expectedType, null));
	}

	@Override
	public T finish(DependencySupplier provider, FileHandle assetFile, SelializedJsonProperties properties) {
		return remove(assetFile);
	}
}
