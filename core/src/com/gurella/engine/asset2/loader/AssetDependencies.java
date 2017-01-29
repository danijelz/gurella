package com.gurella.engine.asset2.loader;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.asset2.AssetIdPool;
import com.gurella.engine.asset2.registry.AssetRegistry;

class AssetDependencies implements DependencyCollector, DependencyProvider {
	private final AssetId loadingAssetId = new AssetId();
	private final AssetId tempAssetId = new AssetId();
	private final ObjectSet<AssetId> dependencies = new ObjectSet<AssetId>();
	private final ObjectMap<AssetId, Object> loadedDependencies = new ObjectMap<AssetId, Object>();
	private final AssetIdPool pool = new AssetIdPool();

	private AssetRegistry registry;

	void init(String fileName, FileType fileType, Class<?> assetType) {
		loadingAssetId.set(fileName, fileType, assetType);
	}

	@Override
	public void addDependency(String fileName, FileType fileType, Class<?> assetType) {
		AssetId dependencyId = pool.obtain().set(fileName, fileType, assetType);
		if (dependencies.add(dependencyId)) {
			loadedDependencies.put(dependencyId, null);
		} else {
			pool.free(dependencyId);
		}
	}

	@Override
	public <T> T getDependency(String depFileName, FileType depFileType, Class<T> depAssetType) {
		tempAssetId.set(depFileName, depFileType, depAssetType);
		return registry.getDependencyAndIncCount(loadingAssetId, tempAssetId);
	}

	public boolean isEmpty() {
		return dependencies.size == 0;
	}

	void reset() {
		for (AssetId assetId : dependencies) {
			pool.free(assetId);
		}
		dependencies.clear();
		loadedDependencies.clear();
	}
}
