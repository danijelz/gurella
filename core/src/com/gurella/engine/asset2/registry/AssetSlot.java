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
	Object asset;

	boolean sticky;
	int refCount = 0;
	final ObjectIntMap<AssetId> dependencies = new ObjectIntMap<AssetId>(4);
	final ObjectSet<AssetId> dependents = new ObjectSet<AssetId>(4);
	final ObjectMap<String, Object> bundledAssets = new ObjectMap<String, Object>();

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

	ObjectMap<String, Object> initBundledAssets() {
		if (asset instanceof Bundle) {
			((Bundle) asset).getBundledAssets(bundledAssets);
		}

		return bundledAssets;
	}

	<T> T getBundledAsset(String bundleId) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("unchecked")
		T casted = (T) bundledAssets.get(bundleId);
		return casted;
	}

	void addBundledAsset(Object bundledAsset, String bundleId) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		bundledAssets.put(bundleId, bundledAsset);
	}

	void removeBundledAsset(String bundleId) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		bundledAssets.remove(bundleId);
	}

	void removeBundledAsset(Object bundledAsset) {
		bundledAssets.remove(getBundleId(bundledAsset));
	}

	String getBundleId(Object bundledAsset) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		if (bundledAsset instanceof BundleAware) {
			return ((BundleAware) bundledAsset).getBundleId();
		}

		for (Entry<String, Object> bundledAssetsEntry : bundledAssets.entries()) {
			if (bundledAssetsEntry.value == bundledAsset) {
				return bundledAssetsEntry.key;
			}
		}

		throw new IllegalStateException();
	}

	public void merge(AssetSlot other) {
		sticky |= other.sticky;
		refCount += other.refCount;
		dependents.addAll(other.dependents);

		for (ObjectIntMap.Entry<AssetId> entry : other.dependencies.entries()) {
			dependencies.put(entry.key, entry.value);
		}

		if (other.bundledAssets.size > 0) {
			for (ObjectMap.Entry<String, Object> entry : other.bundledAssets.entries()) {
				bundledAssets.put(entry.key, entry.value);
			}
		}
	}

	@Override
	public void reset() {
		asset = null;
		sticky = false;
		refCount = 0;
		dependencies.clear();
		dependents.clear();
		bundledAssets.clear();
	}
}
