package com.gurella.engine.asset2.loader;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.asset2.AssetIdPool;
import com.gurella.engine.asset2.registry.AssetRegistry;

class AssetDependencies implements DependencyCollector, DependencyProvider {
	final AssetRegistry registry;
	private final ObjectSet<AssetId> dependencies = new ObjectSet<AssetId>();
	private final AssetIdPool pool = new AssetIdPool();

	AssetDependencies(AssetRegistry registry) {
		this.registry = registry;
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
	}

	@Override
	public <T> T getDependency(String fileName, FileType fileType, Class<T> assetType) {
		return registry.getDependency(fileName, fileType, assetType);
	}
}
