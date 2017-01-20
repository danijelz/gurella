package com.gurella.engine.asset.loader.object;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.asset.AssetRegistry;
import com.gurella.engine.asset.DependencyTracker;
import com.gurella.engine.asset.DependencyTrackerAware;
import com.gurella.engine.serialization.json.AssetProvider;
import com.gurella.engine.serialization.json.JsonInput;
import com.gurella.engine.utils.Values;

public class JsonObjectLoader<T> extends AsynchronousAssetLoader<T, AssetLoaderParameters<T>>
		implements DependencyTrackerAware, AssetProvider {
	private final Class<T> expectedType;
	private final JsonInput input = new JsonInput();
	private final ObjectMap<String, Object> dependencies = new ObjectMap<String, Object>();

	private T result;
	private AssetRegistry registry;
	private DependencyTracker tracker;

	public JsonObjectLoader(FileHandleResolver resolver, Class<T> expectedType) {
		super(resolver);
		this.expectedType = expectedType;
		input.setAssetProvider(this);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<T> parameters) {
		registry = (AssetRegistry) manager;
		result = input.deserialize(expectedType);
	}

	@Override
	public T loadSync(AssetManager manager, String fileName, FileHandle file, AssetLoaderParameters<T> parameters) {
		T object = result;
		result = null;
		registry = null;
		dependencies.clear();
		return object;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<T> parameters) {
		input.init(file);
		return Values.cast(input.getExternalDependencies());
	}

	@Override
	public void setDependencyTracker(DependencyTracker tracker) {
		this.tracker = tracker;
	}

	@Override
	public <A> A getAsset(String fileName) {
		@SuppressWarnings("unchecked")
		A asset = (A) dependencies.get(fileName);
		if (asset != null) {
			tracker.increaseDependencyRefCount(fileName);
			return asset;
		}

		asset = registry.get(fileName);
		dependencies.put(fileName, asset);
		return asset;
	}
}
