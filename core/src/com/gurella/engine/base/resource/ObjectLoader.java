package com.gurella.engine.base.resource;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.serialization.json.JsonInput;
import com.gurella.engine.utils.ValueUtils;

public class ObjectLoader<T> extends AsynchronousAssetLoader<T, AssetLoaderParameters<T>> {
	private final JsonInput input = new JsonInput();
	private final Class<T> expectedType;

	T result;

	public ObjectLoader(FileHandleResolver resolver, Class<T> expectedType) {
		super(resolver);
		this.expectedType = expectedType;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<T> parameters) {
		result = input.deserialize(expectedType);
	}

	@Override
	public T loadSync(AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<T> parameters) {
		T object = result;
		result = null;
		return object;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<T> parameters) {
		input.init(file);
		Array<AssetDescriptor> dependencies = ValueUtils.cast(input.getExternalDependencies());
		return dependencies;
	}
}
