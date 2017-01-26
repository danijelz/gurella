package com.gurella.engine.asset2.registry;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.asset2.bundle.BundleAware;

class AssetSlot implements Poolable {
	private static final ObjectMap<String, Object> emptyBundledAssets = new ObjectMap<String, Object>();

	Object asset;

	boolean sticky;

	int refCount = 0;
	final ObjectIntMap<AssetId> dependencies = new ObjectIntMap<AssetId>(4);
	final ObjectSet<AssetId> dependents = new ObjectSet<AssetId>(4);
	ObjectMap<String, Object> bundledAssets;

	boolean isActive() {
		return sticky || refCount > 0 || dependents.size > 0;
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

	void incDependencyCount(AssetId id) {
		// TODO
		int count = dependencies.get(id, -1);
		if (count < 0) {

		}
	}

	void decDependencyCount(AssetId id) {
		// TODO
		int count = dependencies.get(id, -1);
		if (count < 0) {

		}
	}

	void addDependent(AssetId dependent) {
		dependents.add(dependent);
	}

	boolean removeDependent(AssetId dependent) {
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

	<T> T getBundledAsset(String bundleId) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("unchecked")
		T casted = (T) getBundledAssets().get(bundleId);
		return casted;
	}

	void addBundledAsset(Object bundledAsset, String bundleId) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		getBundledAssets().put(bundleId, bundledAsset);
	}

	void removeBundledAsset(String bundleId) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		getBundledAssets().remove(bundleId);
	}

	void removeBundledAsset(Object bundledAsset) {
		getBundledAssets().remove(getBundleId(bundledAsset));
	}

	String getBundleId(Object bundledAsset) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}
		
		if (bundledAsset instanceof BundleAware) {
			return ((BundleAware) bundledAsset).getBundleId();
		}

		for (Entry<String, Object> bundledAssetsEntry : getBundledAssets().entries()) {
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
		refCount = 0;
		dependencies.clear();
		dependents.clear();
		bundledAssets = null;
	}
}
