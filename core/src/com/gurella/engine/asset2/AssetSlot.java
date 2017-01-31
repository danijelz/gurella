package com.gurella.engine.asset2;

import static com.gurella.engine.asset2.AssetSlot.DependencyActivity.fresh;
import static com.gurella.engine.asset2.AssetSlot.DependencyActivity.obsolete;
import static com.gurella.engine.asset2.AssetSlot.DependencyActivity.steady;
import static com.gurella.engine.asset2.AssetSlot.SlotActivity.active;
import static com.gurella.engine.asset2.AssetSlot.SlotActivity.inactive;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.asset2.bundle.BundleAware;

class AssetSlot implements Poolable {
	Object asset;

	boolean sticky;
	int references = 0;
	int reservations = 0;
	final ObjectIntMap<AssetId> dependencies = new ObjectIntMap<AssetId>(4);
	final ObjectSet<AssetId> dependents = new ObjectSet<AssetId>(4);
	final ObjectMap<String, Object> bundledAssets = new ObjectMap<String, Object>();

	boolean isActive() {
		return sticky || references > 0 || dependents.size > 0 || reservations > 0;
	}

	SlotActivity getActivity() {
		return isActive() ? active : inactive;
	}

	void incReferences() {
		references++;
	}

	SlotActivity decReferences() {
		if (references > 0) {
			references--;
		}
		return getActivity();
	}

	void incReservations() {
		reservations++;
	}

	SlotActivity decReservations() {
		if (reservations > 0) {
			reservations--;
		}
		return getActivity();
	}

	DependencyActivity incDependencyCount(AssetId id) {
		int current = dependencies.getAndIncrement(id, 0, 1);
		return current == 0 ? fresh : steady;
	}

	DependencyActivity decDependencyCount(AssetId id) {
		int ref = dependencies.get(id, -1);
		if (ref < 0) {
			return obsolete;
		} else if (ref > 1) {
			dependencies.getAndIncrement(id, 0, -1);
			return steady;
		} else {
			dependencies.remove(id, 0);
			return obsolete;
		}
	}

	void addDependent(AssetId dependent) {
		dependents.add(dependent);
	}

	SlotActivity removeDependent(AssetId dependent) {
		dependents.remove(dependent);
		return getActivity();
	}

	//TODO rename to getBundledAssets
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

	void addBundledAsset(String bundleId, Object bundledAsset) {
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

	void merge(AssetSlot other) {
		//TODO if(other.reserved) throw ...
		sticky |= other.sticky;
		references += other.references;
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
		references = 0;
		reservations = 0;
		dependencies.clear();
		dependents.clear();
		bundledAssets.clear();
	}

	public enum SlotActivity {
		active, inactive;
	}

	public enum DependencyActivity {
		fresh, steady, obsolete;
	}
}
