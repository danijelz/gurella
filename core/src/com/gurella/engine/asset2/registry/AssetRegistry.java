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

	public <T> T getDependency(String fileName, FileType fileType, Class<T> assetType) {
		tempAssetId.set(fileName, fileType, assetType);
		AssetSlot slot = assetsById.get(tempAssetId);
		slot.incDependencyCount(tempAssetId);
		@SuppressWarnings("unchecked")
		T casted = (T) slot.asset;
		return casted;
	}

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

	public <T> T get(String fileName, Class<T> assetType, String bundleId) {
		return get(fileName, FileType.Internal, assetType, bundleId);
	}

	public <T> T get(String fileName, FileType fileType, Class<T> assetType, String bundleId) {
		synchronized (mutex) {
			tempAssetId.set(fileName, fileType, assetType);
			return _get(tempAssetId, bundleId);
		}
	}

	public <T> T get(AssetId assetId, String bundleId) {
		synchronized (mutex) {
			return _get(assetId, bundleId);
		}
	}

	private <T> T _get(AssetId assetId, String bundleId) {
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
				if (all || ClassReflection.isAssignableFrom(type, asset.getClass())) {
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
			AssetId id = idsByAsset.get(asset);
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

	public void unload(Object asset) {
		synchronized (mutex) {
			AssetId id = idsByAsset.get(asset);
			if (id == null) {
				return;
			}

			Bundle bundle = assetBundle.get(asset);
			if (bundle != null && bundle != asset) {
				return;
			}

			AssetSlot slot = assetsById.get(id);
			if (slot.decRefCount()) {
				return;
			}

			assetUnloadedEvent.post(id.fileName, asset);
			unloadBundledAssets(slot);
			dereferenceDependencies(id, slot);

			assetsById.remove(id);
			assetBundle.remove(asset);
			idsByAsset.remove(asset);

			assetIdPool.free(id);
			assetSlotPool.free(slot);

			if (asset instanceof Poolable) {
				PoolService.free(asset);
			} else {
				DisposablesService.tryDispose(asset);
			}
		}
	}

	private void unloadBundledAssets(AssetSlot slot) {
		ObjectMap<String, Object> bundledAssets = slot.bundledAssets;
		if(bundledAssets == null || bundledAssets.size == 0) {
			return;
		}
		
		for(Object asset : bundledAssets.values()) {
			AssetId id = idsByAsset.get(asset);
		}
		
		// TODO Auto-generated method stub
	}

	private void dereferenceDependencies(AssetId id, AssetSlot slot) {
		for (AssetId dependencyId : slot.dependencies.keys()) {
			AssetSlot dependencySlot = assetsById.get(dependencyId);
			if (!dependencySlot.removeDependent(id)) {
				unload(dependencySlot.asset);
			}
		}
	}

	public void addDependency(Object asset, Object dependency) {
		// TODO getInstance().assetRegistry.addDependency(asset, dependency);
	}

	public void removeDependency(Object asset, Object dependency) {
		// TODO getInstance().assetRegistry.removeDependency(asset, dependency);
	}

	public void replaceDependency(Object asset, Object oldDependency, Object newDependency) {
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
			} else if (idsByAsset.containsKey(asset)) {
				// TODO
			} else if (assetRootBundle != null) {
				removeFromBundle(rootBundleSlot, asset);
			}

			AssetId id = assetIdPool.obtain().set(rootBundleId);
			id.assetType = asset.getClass();

			idsByAsset.put(asset, id);
			assetBundle.put(asset, rootBundle);
			rootBundleSlot.addBundledAsset(asset, bundleId);

			if (asset instanceof Bundle) {
				((Bundle) asset).getBundledAssets(tempBundledAssets);
				for (Entry<String, Object> entry : tempBundledAssets.entries()) {
					if (entry.value != asset) {
						id = assetIdPool.obtain().set(rootBundleId);
						id.assetType = entry.value.getClass();
						idsByAsset.put(asset, id);
						assetBundle.put(asset, rootBundle);
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
		AssetId id = idsByAsset.remove(asset);
		assetBundle.remove(asset);
		bundleSlot.removeBundledAsset(asset);
		assetIdPool.free(id);

		if (asset instanceof Bundle) {
			((Bundle) asset).getBundledAssets(tempBundledAssets);
			for (Entry<String, Object> entry : tempBundledAssets.entries()) {
				Object bundledAsset = entry.value;
				if (bundledAsset != asset) {
					AssetId bundledAssetId = idsByAsset.remove(bundledAsset);
					assetsById.remove(id);
					bundleSlot.removeBundledAsset(entry.key);
					assetIdPool.free(bundledAssetId);
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

	private static class AssetSlotPool extends Pool<AssetSlot> {
		@Override
		protected AssetSlot newObject() {
			return new AssetSlot();
		}
	}
}
