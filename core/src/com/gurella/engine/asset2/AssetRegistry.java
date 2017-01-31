package com.gurella.engine.asset2;

import static com.gurella.engine.asset2.AssetSlot.DependencyActivity.fresh;
import static com.gurella.engine.asset2.AssetSlot.DependencyActivity.obsolete;
import static com.gurella.engine.asset2.AssetSlot.SlotActivity.active;
import static com.gurella.engine.asset2.AssetSlot.SlotActivity.inactive;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset2.AssetSlot.SlotActivity;
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.asset2.bundle.BundleAware;
import com.gurella.engine.asset2.event.AssetLoadedEvent;
import com.gurella.engine.asset2.event.AssetUnloadedEvent;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.pool.PoolService;

class AssetRegistry implements Disposable {
	private final ObjectMap<AssetId, AssetSlot> slotsById = new ObjectMap<AssetId, AssetSlot>();
	private final IdentityMap<Object, AssetId> idsByAsset = new IdentityMap<Object, AssetId>();
	private final IdentityMap<Object, Bundle> assetBundle = new IdentityMap<Object, Bundle>();

	private final AssetIdPool assetIdPool = new AssetIdPool();
	private final AssetSlotPool assetSlotPool = new AssetSlotPool();

	private final AssetId tempAssetId = new AssetId();
	private final ObjectMap<String, Object> tempBundledAssets = new ObjectMap<String, Object>();

	private final AssetUnloadedEvent assetUnloadedEvent = new AssetUnloadedEvent();
	private final AssetLoadedEvent assetLoadedEvent = new AssetLoadedEvent();

	<T> T get(String fileName, FileType fileType, Class<?> assetType, String bundleId) {
		tempAssetId.set(fileName, fileType, assetType);
		AssetSlot slot = slotsById.get(tempAssetId);

		if (slot == null || slot.asset == null) {
			throw new GdxRuntimeException("Asset not loaded: " + fileName);
		}

		if (bundleId == null) {
			@SuppressWarnings("unchecked")
			T asset = (T) slot.asset;
			return asset;
		} else {
			Object asset = slot.asset;
			if (asset instanceof Bundle) {
				return slot.getBundledAsset(bundleId);
			} else {
				throw new IllegalArgumentException("Asset is not a Bundle: " + fileName);
			}
		}
	}

	<T> Array<T> getAll(Class<T> type, Array<T> out) {
		boolean all = type == null || type == Object.class;
		for (Object asset : idsByAsset.keys()) {
			if (all || ClassReflection.isInstance(type, asset)) {
				@SuppressWarnings("unchecked")
				T casted = (T) asset;
				out.add(casted);
			}
		}

		return out;
	}

	<T> boolean isManaged(T asset) {
		return idsByAsset.containsKey(asset);
	}

	<T> AssetId getAssetId(T asset, AssetId out) {
		AssetId id = idsByAsset.get(getAssetOrRootBundle(asset));
		if (id == null) {
			return out.empty();
		} else {
			return out.set(id);
		}
	}

	<T> String getFileName(T asset) {
		AssetId id = idsByAsset.get(getAssetOrRootBundle(asset));
		return id == null ? null : id.fileName;
	}

	<T> FileType getFileType(T asset) {
		AssetId id = idsByAsset.get(getAssetOrRootBundle(asset));
		return id == null ? null : id.fileType;
	}

	boolean isLoaded(String fileName, FileType fileType, Class<?> assetType) {
		tempAssetId.set(fileName, fileType, assetType);
		AssetSlot slot = slotsById.get(tempAssetId);
		return slot != null && slot.asset != null;
	}

	<T> T getIfLoaded(String fileName, FileType fileType, Class<?> assetType, String bundleId) {
		tempAssetId.set(fileName, fileType, assetType);
		AssetSlot slot = slotsById.get(tempAssetId);

		if (slot == null || slot.asset == null) {
			return null;
		}

		slot.incRefCount();

		if (bundleId == null) {
			@SuppressWarnings("unchecked")
			T asset = (T) slot.asset;
			return asset;
		} else {
			return slot.getBundledAsset(bundleId);
		}
	}

	<T> void add(String fileName, FileType fileType, Class<T> assetType, T asset, boolean sticky) {
		if (idsByAsset.containsKey(asset)) {
			throw new IllegalStateException("Asset is already loaded: " + fileName);
		}

		AssetId assetId = assetIdPool.obtain().set(fileName, fileType, assetType);
		idsByAsset.put(asset, assetId);

		AssetSlot slot = assetSlotPool.obtain();
		slotsById.put(assetId, slot);
		populateSlot(assetId, slot, asset, sticky);
	}

	<T> void populateSlot(AssetId assetId, AssetSlot slot, T asset, boolean sticky) {
		slot.asset = asset;
		slot.sticky = sticky;

		if (asset instanceof Bundle) {
			ObjectMap<String, Object> bundledAssets = slot.initBundledAssets();
			if (bundledAssets.size > 0) {
				Bundle bundle = (Bundle) asset;
				for (Object bundledAsset : bundledAssets.values()) {
					idsByAsset.put(bundledAsset, assetId);
					assetBundle.put(bundledAsset, bundle);
				}
			}
		}

		assetLoadedEvent.post(assetId, asset);
	}

	boolean decRefCount(Object asset) {
		Object toRemove = getAssetOrRootBundle(asset);
		AssetId id = idsByAsset.get(toRemove);
		if (id == null) {
			return false;
		}

		AssetSlot slot = slotsById.get(id);
		if (slot.decRefCount() == SlotActivity.inactive) {
			remove(id, slot);
			return true;
		} else {
			return false;
		}
	}

	boolean removeAll(String fileName, FileType fileType) {
		boolean removed = false;
		for (Entries<AssetId, AssetSlot> iter = slotsById.iterator(); iter.hasNext;) {
			Entry<AssetId, AssetSlot> entry = iter.next();
			AssetId assetId = entry.key;
			if (assetId.equalsFile(fileName, fileType)) {
				AssetSlot slot = entry.value;
				iter.remove();
				remove(assetId, slot);
				removed = true;
			}
		}
		return removed;
	}

	private void remove(AssetId id, AssetSlot slot) {
		Object asset = slot.asset;
		assetUnloadedEvent.post(id, asset);
		unloadBundledAssets(slot);
		dereferenceDependencies(id, slot);

		idsByAsset.remove(asset);
		slotsById.remove(id);
		assetIdPool.free(id);
		assetSlotPool.free(slot);

		if (asset instanceof Poolable) {
			PoolService.free(asset);
		} else {
			DisposablesService.tryDispose(asset);
		}
	}

	private void unloadBundledAssets(AssetSlot slot) {
		ObjectMap<String, Object> bundledAssets = slot.bundledAssets;
		if (bundledAssets.size == 0) {
			return;
		}

		for (Object asset : bundledAssets.values()) {
			idsByAsset.remove(asset);
			assetBundle.remove(asset);
		}
	}

	private void dereferenceDependencies(AssetId id, AssetSlot slot) {
		for (AssetId dependencyId : slot.dependencies.keys()) {
			AssetSlot dependencySlot = slotsById.get(dependencyId);
			if (dependencySlot.removeDependent(id) == SlotActivity.inactive) {
				remove(dependencyId, dependencySlot);
			}
		}
	}

	<T> T getDependencyAndIncCount(AssetId dependant, AssetId dependencyId) {
		AssetSlot slot = slotsById.get(dependant);
		AssetSlot dependencySlot = slotsById.get(dependencyId);
		@SuppressWarnings("unchecked")
		T dependency = (T) dependencySlot.asset;
		incDependencyCount(dependant, slot, dependency);
		return dependency;
	}

	void addDependency(Object asset, Object dependency) {
		AssetId id = idsByAsset.get(getAssetOrRootBundle(asset));
		AssetSlot slot = slotsById.get(id);
		incDependencyCount(id, slot, dependency);
	}

	private void incDependencyCount(AssetId id, AssetSlot slot, Object dependency) {
		AssetId dependencyId = idsByAsset.get(dependency);
		if (slot.incDependencyCount(dependencyId) == fresh) {
			AssetSlot dependencySlot = slotsById.get(idsByAsset.get(dependency));
			dependencySlot.addDependent(id);
		}
	}

	public void removeDependency(Object asset, Object dependency) {
		AssetId id = idsByAsset.get(getAssetOrRootBundle(asset));
		AssetSlot slot = slotsById.get(id);
		if (removeDependency(id, slot, dependency) == inactive) {
			remove(id, slot);
		}
	}

	private SlotActivity removeDependency(AssetId id, AssetSlot slot, Object dependency) {
		AssetId dependencyId = idsByAsset.get(dependency);
		if (slot.decDependencyCount(dependencyId) == obsolete) {
			AssetSlot dependencySlot = slotsById.get(dependencyId);
			if (dependencySlot.removeDependent(id) == inactive) {
				remove(dependencyId, dependencySlot);
			}
			return slot.getActivity();
		} else {
			return active;
		}
	}

	void replaceDependency(Object asset, Object oldDependency, Object newDependency) {
		if (oldDependency == newDependency) {
			return;
		}

		if (asset == newDependency) {
			throw new IllegalArgumentException("Asset can't depend on itself.");
		}

		AssetId id = idsByAsset.get(getAssetOrRootBundle(asset));
		AssetSlot slot = slotsById.get(id);

		removeDependency(id, slot, oldDependency);
		incDependencyCount(id, slot, newDependency);
	}

	void addToBundle(Bundle bundle, BundleAware asset) {
		addToBundle(bundle, asset, asset.getBundleId());
	}

	void addToBundle(Bundle bundle, Object asset, String bundleId) {
		Bundle rootBundle = getRootBundle(bundle);
		AssetId rootBundleId = idsByAsset.get(rootBundle);
		AssetSlot rootBundleSlot = slotsById.get(rootBundleId);
		Bundle assetRootBundle = getAssetRootBundle(asset);

		if (assetRootBundle == rootBundle) {
			return;
		}

		assetBundle.put(asset, rootBundle);

		AssetId id = idsByAsset.get(asset);
		AssetSlot slot = id == null ? null : slotsById.remove(id);
		if (slot != null) {
			ObjectMap<String, Object> bundledAssets = slot.bundledAssets;
			if (bundledAssets != null && bundledAssets.size > 0) {
				for (Object bundledAsset : slot.bundledAssets.values()) {
					assetBundle.put(bundledAsset, rootBundle);
				}
			}
			rootBundleSlot.merge(slot);
			assetSlotPool.free(slot);
			return;
		}

		if (assetRootBundle != null) {
			removeFromBundle(rootBundleSlot, asset);
		}

		if (id == null) {
			id = assetIdPool.obtain().set(rootBundleId);
			id.assetType = asset.getClass();
			idsByAsset.put(asset, id);
		}

		rootBundleSlot.addBundledAsset(bundleId, asset);

		if (asset instanceof Bundle) {
			((Bundle) asset).getBundledAssets(tempBundledAssets);
			for (Entry<String, Object> entry : tempBundledAssets.entries()) {
				Object bundledAsset = entry.value;
				if (bundledAsset != asset) {
					id = assetIdPool.obtain().set(rootBundleId);
					id.assetType = bundledAsset.getClass();
					idsByAsset.put(bundledAsset, id);
					assetBundle.put(bundledAsset, rootBundle);
					rootBundleSlot.addBundledAsset(entry.key, asset);
				}
			}
			tempBundledAssets.clear();
		}
	}

	void removeFromBundle(Bundle bundle, Object asset) {
		if (bundle == asset) {
			throw new IllegalArgumentException("Use unload.");
		}

		Bundle rootBundle = getRootBundle(bundle);
		AssetId rootBundleId = idsByAsset.get(rootBundle);
		AssetSlot rootBundleSlot = slotsById.get(rootBundleId);
		removeFromBundle(rootBundleSlot, asset);
	}

	private void removeFromBundle(AssetSlot bundleSlot, Object asset) {
		idsByAsset.remove(asset);
		assetBundle.remove(asset);
		bundleSlot.removeBundledAsset(asset);

		if (asset instanceof Bundle) {
			((Bundle) asset).getBundledAssets(tempBundledAssets);
			for (Entry<String, Object> entry : tempBundledAssets.entries()) {
				Object bundledAsset = entry.value;
				if (bundledAsset != asset) {
					idsByAsset.remove(bundledAsset);
					assetBundle.remove(bundledAsset);
					bundleSlot.removeBundledAsset(entry.key);
				}
			}
			tempBundledAssets.clear();
		}
	}

	String getBundleId(Object asset) {
		if (asset instanceof BundleAware) {
			return ((BundleAware) asset).getBundleId();
		}

		Bundle bundle = assetBundle.get(asset);
		AssetId id = idsByAsset.get(getRootBundle(bundle));
		AssetSlot slot = slotsById.get(id);
		return slot.getBundleId(asset);
	}

	private Bundle getRootBundle(Bundle bundle) {
		Bundle last = bundle;
		while (true) {
			Bundle temp = assetBundle.get(last);
			if (temp == null || temp == last) {
				return last;
			} else {
				last = temp;
			}
		}
	}

	private Bundle getAssetRootBundle(Object asset) {
		Bundle bundle = assetBundle.get(asset);
		return bundle == null ? null : getRootBundle(bundle);
	}

	private Object getAssetOrRootBundle(Object asset) {
		Bundle bundle = assetBundle.get(asset);
		return bundle == null ? asset : getRootBundle(bundle);
	}

	@Override
	public void dispose() {
		removeAll();
		slotsById.clear();
		idsByAsset.clear();
		assetBundle.clear();
		assetIdPool.clear();
		assetSlotPool.clear();
		tempBundledAssets.clear();
		tempAssetId.empty();
	}

	private void removeAll() {
		for (Entries<AssetId, AssetSlot> iter = slotsById.iterator(); iter.hasNext;) {
			Entry<AssetId, AssetSlot> entry = iter.next();
			AssetId assetId = entry.key;
			AssetSlot slot = entry.value;
			iter.remove();
			remove(assetId, slot);
		}
	}

	private static class AssetSlotPool extends Pool<AssetSlot> {
		@Override
		protected AssetSlot newObject() {
			return new AssetSlot();
		}
	}
}
