package com.gurella.engine.asset2;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.asset2.loader.AssetsLoader;
import com.gurella.engine.asset2.persister.AssetsPersister;
import com.gurella.engine.asset2.registry.AssetRegistry;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.async.SimpleAsyncCallback;

public class AssetsManager {
	private final Object mutex = new Object();

	private final Files files = Gdx.files;
	private final AssetRegistry registry = new AssetRegistry();
	private final AssetsLoader loader = new AssetsLoader();
	private final AssetsPersister persister = new AssetsPersister(registry);

	public <T> void loadAsync(AsyncCallback<T> callback, String fileName, FileType fileType, Class<T> assetType,
			int priority) {
		T asset;
		synchronized (mutex) {
			asset = registry.getIfLoaded(fileName, fileType, assetType, null);
			if (asset == null) {
				FileHandle file = null;//TODO resolve by config
				loader.load(callback, file, assetType, priority);
			}
		}

		if (asset != null) {
			callback.onProgress(1f);
			callback.onSuccess(asset);
		}
	}

	public <T> T load(String fileName, FileType fileType, Class<T> assetType) {
		synchronized (mutex) {
			T asset = registry.getIfLoaded(fileName, fileType, assetType, null);
			if (asset != null) {
				return asset;
			}

			SimpleAsyncCallback<T> callback = SimpleAsyncCallback.obtain();
			FileHandle file = null;//TODO resolve by config
			loader.load(callback, file, assetType, Integer.MAX_VALUE);

			while (!callback.isDone()) {
				loader.update();
				ThreadUtils.yield();
			}

			if (callback.isDoneWithException()) {
				Throwable exception = callback.getException();
				callback.free();
				throw new RuntimeException("Error loading asset " + fileName, exception);
			} else {
				asset = callback.getValue();
				callback.free();
				return asset;
			}
		}
	}

	public boolean isLoaded(String fileName) {
		return registry.isLoaded(fileName, FileType.Internal, Assets.getAssetClass(fileName));
	}

	public <T> boolean unload(T asset) {
		return registry.remove(asset);
	}

	public <T> T get(String fileName, FileType fileType, Class<T> assetType, String bundleId) {
		return registry.get(fileName, fileType, assetType, bundleId);
	}

	public <T> Array<T> getAll(Class<T> type, Array<T> out) {
		return registry.getAll(type, out);
	}

	public <T> String getFileName(T asset) {
		return registry.getFileName(asset);
	}

	public <T> FileType getFileType(T asset) {
		return registry.getFileType(asset);
	}

	public <T> AssetId getId(T asset, AssetId out) {
		return registry.getAssetId(asset, out);
	}

	public boolean isManaged(Object asset) {
		return registry.isManaged(asset);
	}

	public <T> void save(T asset, FileHandle handle, boolean sticky) {
		persister.persist(asset, handle);
	}

	public void delete(String fileName) {
		getInstance().assetRegistry.delete(fileName);
	}

	public void addDependency(Object asset, Object dependency) {
		registry.addDependency(asset, dependency);
	}

	public void removeDependency(Object asset, Object dependency) {
		registry.removeDependency(asset, dependency);
	}

	public void replaceDependency(Object asset, Object oldDependency, Object newDependency) {
		registry.replaceDependency(asset, oldDependency, newDependency);
	}

	public void addToBundle(Bundle bundle, Object asset, String internalId) {
		registry.addToBundle(bundle, asset, internalId);
	}

	public void removeFromBundle(Bundle bundle, Object asset) {
		registry.removeFromBundle(bundle, asset);
	}

	public String getBundledId(Object asset) {
		return registry.getBundleId(asset);
	}
}
