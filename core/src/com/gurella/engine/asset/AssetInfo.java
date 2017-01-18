package com.gurella.engine.asset;

import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.IdentityMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

class AssetInfo implements Poolable {
	private static final IdentityMap<String, Object> emptyBundledAssets = new IdentityMap<String, Object>();

	Object asset;
	boolean sticky;
	int refCount = 0;
	final ObjectSet<String> dependencies = new ObjectSet<String>(4);
	final ObjectSet<String> dependents = new ObjectSet<String>(4);
	private IdentityMap<String, Object> bundledAssets;

	public static AssetInfo obtain() {
		return PoolService.obtain(AssetInfo.class);
	}

	private AssetInfo() {
	}

	@SuppressWarnings("unchecked")
	<T> T getAsset() {
		return (T) asset;
	}

	public <T> T getBundledAsset(String internalId) {
		if (bundledAssets == null) {
			bundledAssets = asset instanceof Bundle ? ((Bundle) asset).getBundledAssets() : emptyBundledAssets;
		}

		@SuppressWarnings("unchecked")
		T casted = (T) bundledAssets.get(internalId);
		return casted;
	}

	private void initBundledAssets() {
		if (bundledAssets == null) {

		}
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

	void addDependency(String dependency) {
		dependencies.add(dependency);
	}

	void removeDependency(String dependency) {
		dependencies.remove(dependency);
	}

	void addDependent(String dependent) {
		dependents.add(dependent);
	}

	void removeDependent(String dependent) {
		dependents.remove(dependent);
	}

	void addBundledAsset(String internalId, Object bundledAsset) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		if (bundledAssets == null) {
			bundledAssets = ((Bundle) asset).getBundledAssets();
		}

		bundledAssets.put(internalId, bundledAsset);
	}

	void removeBundledAsset(String internalId) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		if (bundledAssets == null) {
			bundledAssets = ((Bundle) asset).getBundledAssets();
		}

		bundledAssets.remove(internalId);
	}

	boolean isActive() {
		return sticky || refCount > 0 || dependents.size > 0;
	}

	public String getBundledAssetInternalId(Object bundledAsset) {
		for (Entry<String, Object> bundledAssetsEntry : bundledAssets.entries()) {
			if (bundledAssetsEntry.value == asset) {
				return bundledAssetsEntry.key;
			}
		}

		throw new IllegalStateException();
	}

	@Override
	public void reset() {
		asset = null;
		sticky = false;
		refCount = 0;
		dependencies.clear();
		dependents.clear();
		bundledAssets = null;
	}

	void free() {
		PoolService.free(this);
	}
}
