package com.gurella.engine.asset;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.pool.PoolService;

class AssetInfo implements DependencyTracker, Poolable {
	private static final ObjectMap<String, Object> emptyBundledAssets = new ObjectMap<String, Object>();

	Object asset;
	boolean sticky;
	FileType fileType;
	int refCount = 0;
	final ObjectIntMap<String> dependencies = new ObjectIntMap<String>(4);
	final ObjectSet<String> dependents = new ObjectSet<String>(4);
	private ObjectMap<String, Object> bundledAssets;

	public static AssetInfo obtain() {
		return PoolService.obtain(AssetInfo.class);
	}

	private AssetInfo() {
	}

	@SuppressWarnings("unchecked")
	<T> T getAsset() {
		return (T) asset;
	}

	void incRefCount() {
		refCount++;
	}

	boolean decRefCount() {
		if (refCount > 0) {
			refCount--;
		}
		return isActive();
	}

	@Override
	public void increaseDependencyRefCount(String dependency) {
		addDependency(dependency);
	}

	int addDependency(String dependency) {
		return dependencies.getAndIncrement(dependency, 0, 1) + 1;
	}

	int removeDependency(String dependency) {
		int ref = dependencies.get(dependency, -1);
		if (ref < 0) {
			return ref;
		} else if (ref > 1) {
			dependencies.getAndIncrement(dependency, 0, -1);
			return ref - 1;
		} else {
			dependencies.remove(dependency, 0);
			return 0;
		}
	}

	void addDependent(String dependent) {
		dependents.add(dependent);
	}

	boolean removeDependent(String dependent) {
		dependents.remove(dependent);
		return isActive();
	}

	ObjectMap<String, Object> getBundledAssets() {
		if (bundledAssets == null) {
			if (asset instanceof Bundle) {
				bundledAssets = ((Bundle) asset).getBundledAssets(new ObjectMap<String, Object>());
			} else {
				bundledAssets = emptyBundledAssets;
			}
		}

		return bundledAssets;
	}

	public <T> T getBundledAsset(String internalId) {
		@SuppressWarnings("unchecked")
		T casted = (T) getBundledAssets().get(internalId);
		return casted;
	}

	void addBundledAsset(String internalId, Object bundledAsset) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		getBundledAssets().put(internalId, bundledAsset);
	}

	void removeBundledAsset(String internalId) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		getBundledAssets().remove(internalId);
	}

	boolean isActive() {
		return sticky || refCount > 0 || dependents.size > 0;
	}

	public String getBundledAssetInternalId(Object bundledAsset) {
		for (Entry<String, Object> bundledAssetsEntry : bundledAssets.entries()) {
			if (bundledAssetsEntry.value == bundledAsset) {
				return bundledAssetsEntry.key;
			}
		}

		throw new IllegalStateException();
	}

	@Override
	public void reset() {
		asset = null;
		sticky = false;
		fileType = null;
		refCount = 0;
		dependencies.clear();
		dependents.clear();
		bundledAssets = null;
	}

	void free() {
		PoolService.free(this);
	}
}
