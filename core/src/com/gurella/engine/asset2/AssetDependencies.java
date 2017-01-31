package com.gurella.engine.asset2;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.gurella.engine.asset2.loader.DependencyCollector;
import com.gurella.engine.asset2.loader.DependencyProvider;
import com.gurella.engine.asset2.properties.AssetProperties;

class AssetDependencies implements DependencyCollector, DependencyProvider {
	private AssetId loadingAssetId;
	private AssetsManager manager;

	private AssetId propertiesAssetId;
	private final ObjectMap<AssetId, Dependency<?>> dependencies = new ObjectMap<AssetId, Dependency<?>>();

	private final AssetId tempAssetId = new AssetId();

	void init(AssetId loadingAssetId, AssetsManager manager) {
		this.manager = manager;
		this.loadingAssetId = loadingAssetId;
	}

	void addPropertiesDependency(String fileName, FileType fileType, Class<?> assetType) {
		if (!dependencies.containsKey(tempAssetId.set(fileName, fileType, assetType))) {
			Dependency<Object> dependency = manager.reserveDependency(fileName, fileType, assetType);
			propertiesAssetId = dependency.getAssetId();
			dependencies.put(propertiesAssetId, dependency);
		}
	}

	<T> AssetProperties<T> getProperties() {
		return propertiesAssetId == null ? null : this.<AssetProperties<T>> getDependency(propertiesAssetId);
	}

	@Override
	public void addDependency(String fileName, FileType fileType, Class<?> assetType) {
		if (!dependencies.containsKey(tempAssetId.set(fileName, fileType, assetType))) {
			Dependency<Object> dependency = manager.reserveDependency(fileName, fileType, assetType);
			dependencies.put(dependency.getAssetId(), dependency);
		}
	}

	@Override
	public <T> T getDependency(String depFileName, FileType depFileType, Class<T> depAssetType) {
		return getDependency(tempAssetId.set(depFileName, depFileType, depAssetType));
	}

	private <T> T getDependency(AssetId assetId) {
		@SuppressWarnings("unchecked")
		Dependency<T> dependency = (Dependency<T>) dependencies.get(assetId);
		dependency.incDependencyCount(loadingAssetId);
		return dependency.getAsset();
	}

	boolean isEmpty() {
		return dependencies.size == 0;
	}

	boolean allResolved() {
		int size = dependencies.size;
		if (size == 0) {
			return true;
		}

		for (Entry<AssetId, Dependency<?>> entry : dependencies.entries()) {
			Dependency<?> dependency = entry.value;
			if (dependency.getProgress() < 1) {
				return false;
			}
		}

		return true;
	}

	float getProgress() {
		int size = dependencies.size;
		if (size == 0) {
			return 1;
		}

		float progres = 0;
		for (Entry<AssetId, Dependency<?>> entry : dependencies.entries()) {
			Dependency<?> dependency = entry.value;
			progres += dependency == null ? 0 : dependency.getProgress();
		}

		return Math.min(1, progres / size);
	}

	void unreserveDependencies(boolean releaseDependencies) {
		// TODO Auto-generated method stub

	}

	void reset() {
		loadingAssetId = null;
		propertiesAssetId = null;
		propertiesAssetId = null;
		dependencies.clear();
	}
}
