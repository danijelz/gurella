package com.gurella.engine.asset2.loader.object;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.loader.DependencyCollector;
import com.gurella.engine.asset2.loader.DependencyProvider;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.serialization.json.JsonInput;

public class JsonObjectLoader2<T> implements AssetLoader<T, T, AssetProperties<T>> {
	private final Class<T> expectedType;
	private final JsonInput input = new JsonInput();

	public JsonObjectLoader2(Class<T> expectedType) {
		this.expectedType = expectedType;
	}

	@Override
	public T loadAsyncData(DependencyProvider provider, FileHandle file, AssetProperties<T> properties) {
		return input.deserialize(expectedType);
	}

	@Override
	public T consumeAsyncData(DependencyProvider provider, FileHandle file, AssetProperties<T> properties,
			T asyncData) {
		return asyncData;
	}

	@Override
	public void injectDependencies(DependencyCollector collector, FileHandle file, AssetProperties<T> properties) {
		// TODO Auto-generated method stub
		// input.init(file);
		// input.getExternalDependencies()
	}
}
