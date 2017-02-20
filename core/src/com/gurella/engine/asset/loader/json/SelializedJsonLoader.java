package com.gurella.engine.asset.loader.json;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;
import com.gurella.engine.metatype.serialization.json.JsonInput;
import com.gurella.engine.metatype.serialization.json.JsonInput.SerializedObject;
import com.gurella.engine.utils.PoolableJsonReader;

public class SelializedJsonLoader<T> extends BaseAssetLoader<T, SelializedJsonProperties> {
	private final SerializedObjectPool pool = new SerializedObjectPool();
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
		LoaderSerializedObject serializedObject = pool.obtain();
		serializedObject.init(reader.parse(assetFile), collector, assetFile);
		put(assetFile, serializedObject);
	}

	@Override
	public void processAsync(DependencySupplier supplier, FileHandle assetFile, SelializedJsonProperties properties) {
		LoaderSerializedObject serializedObject = get(assetFile);
		serializedObject.supplier = supplier;
		T deserialized = input.deserialize(serializedObject, expectedType);
		put(assetFile, deserialized);
		reader.free(serializedObject.poolValue);
		pool.free(serializedObject);
	}

	@Override
	public T finish(DependencySupplier supplier, FileHandle assetFile, SelializedJsonProperties properties) {
		return remove(assetFile);
	}

	private static class LoaderSerializedObject extends SerializedObject {
		JsonValue poolValue;
		FileType fileType;
		DependencySupplier supplier;

		void init(JsonValue rootValue, DependencyCollector collector, FileHandle assetFile) {
			super.init(rootValue);
			poolValue = rootValue;
			this.fileType = assetFile.type();
			for (int i = 0, n = getExternalDependenciesCount(); i < n; i++) {
				String fileName = getExternalDependencyPath(i);
				Class<?> assetType = getExternalDependencyType(i);
				collector.addDependency(fileName, fileType, assetType);
			}
		}

		@Override
		protected <T> T getExternalDependency(String dependencyPath, Class<?> dependencyType, String bundleId) {
			return supplier.getDependency(dependencyPath, fileType, dependencyType, bundleId);
		}

		@Override
		public void reset() {
			super.reset();
			poolValue = null;
			fileType = null;
			supplier = null;
		}
	}

	private static class SerializedObjectPool extends Pool<LoaderSerializedObject> {
		@Override
		protected LoaderSerializedObject newObject() {
			return new LoaderSerializedObject();
		}
	}
}
