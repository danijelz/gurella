package com.gurella.engine.asset2.loader;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.asset2.AssetIdPool;
import com.gurella.engine.asset2.registry.AssetRegistry;

class AssetDependencies implements DependencyCollector, DependencyProvider {
	private final AssetId loadingAssetId = new AssetId();
	private final AssetId tempAssetId = new AssetId();
	private final ObjectSet<AssetId> dependencies = new ObjectSet<AssetId>();
	private final AssetIdPool pool = new AssetIdPool();

	private AssetRegistry registry;

	void init(AssetRegistry registry, String fileName, FileType fileType, Class<?> assetType) {
		loadingAssetId.set(fileName, fileType, assetType);
	}

	@Override
	public void addDependency(String fileName, FileType fileType, Class<?> assetType) {
		AssetId dependency = pool.obtain();
		dependency.fileName = fileName;
		dependency.fileType = fileType;
		dependency.assetType = assetType;
		dependencies.add(dependency);
	}

	void clear() {
		for (AssetId assetId : dependencies) {
			pool.free(assetId);
		}
		dependencies.clear();
	}

	@Override
	public <T> T getDependency(String depFileName, FileType depFileType, Class<T> depAssetType) {
		tempAssetId.set(depFileName, depFileType, depAssetType);
		return registry.getDependencyAndIncCount(loadingAssetId, tempAssetId);
	}

	public boolean isEmpty() {
		return dependencies.size == 0;
	}
}
