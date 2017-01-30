package com.gurella.engine.asset2;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.asset2.loader.DependencyCollector;
import com.gurella.engine.asset2.loader.DependencyProvider;

class AssetDependencies implements DependencyCollector, DependencyProvider {
	private final AssetId loadingAssetId = new AssetId();
	private final AssetId tempAssetId = new AssetId();
	private final ObjectMap<AssetId, AssetSlot> dependencies = new ObjectMap<AssetId, AssetSlot>();
	private final AssetIdPool pool = new AssetIdPool();

	private AssetRegistry registry;

	void init(String fileName, FileType fileType, Class<?> assetType) {
		loadingAssetId.set(fileName, fileType, assetType);
	}

	@Override
	public void addDependency(String fileName, FileType fileType, Class<?> assetType) {
		if (!dependencies.containsKey(tempAssetId.set(fileName, fileType, assetType))) {
			dependencies.put(pool.obtain().set(fileName, fileType, assetType), null);
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

	public float getProgress() {
		int size = dependencies.size;
		if (size == 0) {
			return 1;
		}

		float progres = 0;
		for (int i = 0; i < size; i++) {
			AssetLoadingTask<?, ?> dependency = dependencies.get(i);
			progres += dependency.progress;
		}

		return Math.min(1, progres / size);
	}

	void reset() {
		for (AssetId assetId : dependencies.keys()) {
			pool.free(assetId);
		}
		dependencies.clear();
	}
}
