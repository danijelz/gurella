package com.gurella.engine.asset2.registry;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.asset2.AssetIdPool;
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.asset2.bundle.BundleAware;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.pool.PoolService;

public class AssetRegistry {
	private final AssetUnloadedEvent assetUnloadedEvent = new AssetUnloadedEvent();

	private final Object mutex = new Object();
	private final ObjectMap<AssetId, AssetSlot> assetsById = new ObjectMap<AssetId, AssetSlot>();
	private final IdentityMap<Object, AssetId> idsByAsset = new IdentityMap<Object, AssetId>();
	private final IdentityMap<Object, Bundle> assetBundle = new IdentityMap<Object, Bundle>();

	private final AssetIdPool assetIdPool = new AssetIdPool();
	private final AssetSlotPool assetSlotPool = new AssetSlotPool();

	private final AssetId tempAssetId = new AssetId();
	private final ObjectMap<String, Object> tempBundledAssets = new ObjectMap<String, Object>();

	public <T> T get(String fileName) {
		return get(fileName, FileType.Internal, Assets.<T> getAssetClass(fileName), null);
	}

	public <T> T get(String fileName, Class<T> assetType) {
		return get(fileName, FileType.Internal, assetType, null);
	}

	public <T> T get(String fileName, FileType fileType, Class<T> assetType) {
		return get(fileName, fileType, assetType, null);
	}

	public <T> T get(AssetId assetId) {
		return get(assetId, null);
	}

	public <T> T get(String fileName, String bundleId) {
		return get(fileName, FileType.Internal, Assets.<T> getAssetClass(fileName), bundleId);
	}

	public <T> T get(String fileName, Class<?> assetType, String bundleId) {
		return get(fileName, FileType.Internal, assetType, bundleId);
	}

	public <T> T get(String fileName, FileType fileType, Class<?> assetType, String bundleId) {
		synchronized (mutex) {
			tempAssetId.set(fileName, fileType, assetType);
			return getAndValidate(tempAssetId, bundleId);
		}
	}

	public <T> T get(AssetId assetId, String bundleId) {
		synchronized (mutex) {
			return getAndValidate(assetId, bundleId);
		}
	}

	private <T> T getAndValidate(AssetId assetId, String bundleId) {
		AssetSlot slot = assetsById.get(tempAssetId);

		if (slot == null || slot.asset == null) {
			throw new GdxRuntimeException("Asset not loaded: " + assetId.fileName);
		}

		if (bundleId == null) {
			@SuppressWarnings("unchecked")
			T asset = (T) slot.asset;
			return asset;
		} else {
			return slot.getBundledAsset(bundleId);
		}
	}

	public <T> Array<T> getAll(Class<T> type, Array<T> out) {
		synchronized (mutex) {
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
	}

	public <T> boolean containsAsset(T asset) {
		synchronized (mutex) {
			return idsByAsset.containsKey(asset);
		}
	}

	public <T> AssetId getAssetId(T asset, AssetId out) {
		synchronized (mutex) {
			out.reset();
			AssetId id = idsByAsset.get(getAssetOrRootBundle(asset));
			return id == null ? out : out.set(id);
		}
	}

	public boolean isLoaded(String fileName) {
		return isLoaded(fileName, FileType.Internal, Assets.getAssetClass(fileName));
	}

	public boolean isLoaded(String fileName, Class<?> assetType) {
		return isLoaded(fileName, FileType.Internal, assetType);
	}

	public boolean isLoaded(String fileName, FileType fileType, Class<?> assetType) {
		synchronized (mutex) {
			tempAssetId.set(fileName, fileType, assetType);
			return assetsById.containsKey(tempAssetId);
		}
	}

	public boolean isLoaded(AssetId id) {
		synchronized (mutex) {
			return assetsById.containsKey(id);
		}
	}

	public <T> void add(String fileName, FileType fileType, Class<T> assetType, T asset, boolean sticky) {
		synchronized (mutex) {
			if (idsByAsset.containsKey(asset)) {
				throw new IllegalStateException("Asset is already loaded: " + fileName);
			}

			AssetId assetId = assetIdPool.obtain().set(fileName, fileType, assetType);
			idsByAsset.put(asset, assetId);

			AssetSlot slot = assetSlotPool.obtain();
			assetsById.put(assetId, slot);
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
		}
	}

	public boolean remove(Object asset) {
		synchronized (mutex) {
			Object toRemove = getAssetOrRootBundle(asset);
			AssetId id = idsByAsset.get(toRemove);
			if (id == null) {
				return false;
			}

			AssetSlot slot = assetsById.get(id);
			if (!slot.decRefCount()) {
				remove(id, slot);
				return true;
			} else {
				return false;
			}
		}
	}

	private void remove(AssetId id, AssetSlot slot) {
		Object asset = slot.asset;
		assetUnloadedEvent.post(id.fileName, asset);
		unloadBundledAssets(slot);
		dereferenceDependencies(id, slot);

		idsByAsset.remove(asset);
		assetsById.remove(id);
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
			AssetSlot dependencySlot = assetsById.get(dependencyId);
			if (!dependencySlot.removeDependent(id)) {
				remove(dependencyId, dependencySlot);
			}
		}
	}

	public <T> T getDependency(String fileName, FileType fileType, Class<T> assetType) {
		synchronized (mutex) {
			tempAssetId.set(fileName, fileType, assetType);
			AssetSlot slot = assetsById.get(tempAssetId);
			slot.incDependencyCount(tempAssetId);
			@SuppressWarnings("unchecked")
			T casted = (T) slot.asset;
			return casted;
		}
	}

	public void addDependency(Object asset, Object dependency) {
		synchronized (mutex) {
			AssetId id = idsByAsset.get(getAssetOrRootBundle(asset));
			AssetSlot slot = assetsById.get(id);
			slot.incDependencyCount(idsByAsset.get(dependency));
		}
	}

	public void removeDependency(Object asset, Object dependency) {
		synchronized (mutex) {
			AssetId id = idsByAsset.get(getAssetOrRootBundle(asset));
			AssetSlot slot = assetsById.get(id);
			if (!slot.decDependencyCount(idsByAsset.get(dependency))) {
				remove(id, slot);
			}
		}
	}

	public void replaceDependency(Object asset, Object oldDependency, Object newDependency) {
		if (oldDependency == newDependency) {
			return;
		}

		if (asset == newDependency) {
			throw new IllegalArgumentException("Asset can't depend on itself.");
		}

		synchronized (mutex) {
			AssetId id = idsByAsset.get(getAssetOrRootBundle(asset));
			AssetSlot slot = assetsById.get(id);
		}
		// TODO getInstance().assetRegistry.replaceDependency(asset, oldDependency, newDependency);
	}

	public void addToBundle(Bundle bundle, BundleAware asset) {
		addToBundle(bundle, asset, asset.getBundleId());
	}

	public void addToBundle(Bundle bundle, Object asset, String bundleId) {
		synchronized (mutex) {
			Bundle rootBundle = getRootBundle(bundle);
			AssetId rootBundleId = idsByAsset.get(rootBundle);
			AssetSlot rootBundleSlot = assetsById.get(rootBundleId);
			Bundle assetRootBundle = getAssetRootBundle(asset);

			if (assetRootBundle == rootBundle) {
				return;
			}

			assetBundle.put(asset, rootBundle);

			AssetId id = idsByAsset.get(asset);
			AssetSlot slot = id == null ? null : assetsById.remove(id);
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

			rootBundleSlot.addBundledAsset(asset, bundleId);

			if (asset instanceof Bundle) {
				((Bundle) asset).getBundledAssets(tempBundledAssets);
				for (Entry<String, Object> entry : tempBundledAssets.entries()) {
					Object bundledAsset = entry.value;
					if (bundledAsset != asset) {
						id = assetIdPool.obtain().set(rootBundleId);
						id.assetType = bundledAsset.getClass();
						idsByAsset.put(bundledAsset, id);
						assetBundle.put(bundledAsset, rootBundle);
						rootBundleSlot.addBundledAsset(asset, entry.key);
					}
				}
				tempBundledAssets.clear();
			}
		}
	}

	public void removeFromBundle(Bundle bundle, Object asset) {
		if (bundle == asset) {
			throw new IllegalArgumentException("Use unload.");
		}

		synchronized (mutex) {
			Bundle rootBundle = getRootBundle(bundle);
			AssetId rootBundleId = idsByAsset.get(rootBundle);
			AssetSlot rootBundleSlot = assetsById.get(rootBundleId);
			removeFromBundle(rootBundleSlot, asset);
		}
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

	public String getBundleId(Object asset) {
		if (asset instanceof BundleAware) {
			return ((BundleAware) asset).getBundleId();
		}

		synchronized (mutex) {
			Bundle bundle = assetBundle.get(asset);
			AssetId id = idsByAsset.get(getRootBundle(bundle));
			AssetSlot slot = assetsById.get(id);
			return slot.getBundleId(asset);
		}
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

	private static class AssetSlotPool extends Pool<AssetSlot> {
		@Override
		protected AssetSlot newObject() {
			return new AssetSlot();
		}
	}
}
