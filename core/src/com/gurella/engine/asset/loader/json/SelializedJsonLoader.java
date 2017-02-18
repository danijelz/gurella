package com.gurella.engine.asset.loader.json;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;
import com.gurella.engine.metatype.serialization.json.JsonInput;
import com.gurella.engine.metatype.serialization.json.JsonInput.SerializedObject;
import com.gurella.engine.utils.PoolableJsonReader;

public class SelializedJsonLoader<T> extends BaseAssetLoader<T, SelializedJsonProperties> {
	private final PoolableJsonReader reader = new PoolableJsonReader();

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
		SerializedObject serializedObject = new SerializedObject();
		serializedObject.file = assetFile;
		input.init(assetFile, serializedObject);
		FileType fileType = assetFile.type();
		Array<String> dependencyPaths = serializedObject.dependencyPaths;
		Array<Class<?>> dependencyTypes = serializedObject.dependencyTypes;
		for (int i = 0, n = dependencyPaths.size; i < n; i++) {
			collector.addDependency(dependencyPaths.get(i), fileType, dependencyTypes.get(i));
		}
		put(assetFile, serializedObject);
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle assetFile, SelializedJsonProperties properties) {
		SerializedObject serializedObject = get(assetFile);
		T deserialized = input.deserialize(provider, expectedType, serializedObject);
		put(assetFile, deserialized);
		//TODO free JsonValues
	}

	@Override
	public T finish(DependencySupplier provider, FileHandle assetFile, SelializedJsonProperties properties) {
		return remove(assetFile);
	}
}
