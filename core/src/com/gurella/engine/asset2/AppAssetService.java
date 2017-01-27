package com.gurella.engine.asset2;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.asset2.config.AssetConfig;
import com.gurella.engine.asset2.loader.AssetLoaders;
import com.gurella.engine.asset2.loader.AssetLoadingExecutor;
import com.gurella.engine.asset2.persister.AssetPersisters;
import com.gurella.engine.asset2.registry.AssetRegistry;
import com.gurella.engine.async.AsyncCallback;

public class AppAssetService {
	private final Object mutex = new Object();

	private final Files files = Gdx.files;
	private final AssetRegistry registry = new AssetRegistry();
	private final AssetLoaders loaders = new AssetLoaders(mutex, registry);
	private final AssetLoadingExecutor executor = new AssetLoadingExecutor();
	private final AssetPersisters persisters = new AssetPersisters(registry);

	private final AssetId tempAssetId = new AssetId();

	// TODO
	public <T> AssetConfig<T> getAssetConfig(String fileName) {
		return null;
	}

	public <T> void loadAsync(AsyncCallback<T> callback, String fileName) {
		Class<T> type = getAssetType(fileName);
		loadAsync(fileName, type, callback, priority, false);
	}

	public <T> void loadAsync(AsyncCallback<T> callback, String fileName, int priority) {
		Class<T> assetType = getAssetType(fileName);
		loadAsync(fileName, assetType, callback, priority, false);
	}

	public <T> void loadAsync(AsyncCallback<T> callback, String fileName, Class<T> assetType, int priority) {
		getInstance().assetRegistry.load(fileName, type, parameters, callback, priority, sticky);
	}

	public <T> void loadAsync(AsyncCallback<T> callback, String fileName, FileType fileType, Class<T> assetType,
			int priority) {
		getInstance().assetRegistry.load(fileName, assetType, props, callback, priority, sticky);
	}

	public <T> T load(String fileName) {
		synchronized (mutex) {
			tempAssetId.set(fileName);
			return getLoadedOrLoad(tempAssetId, 0);
		}
	}

	public <T> T load(String fileName, Class<T> assetType) {
		synchronized (mutex) {
			tempAssetId.set(fileName, assetType);
			return getLoadedOrLoad(tempAssetId, 0);
		}
	}

	public <T> T load(String fileName, Class<T> assetType, int priority) {
		synchronized (mutex) {
			tempAssetId.set(fileName, assetType);
			return getLoadedOrLoad(tempAssetId, priority);
		}
	}

	public <T> T load(String fileName, FileType fileType, Class<T> assetType, int priority) {
		synchronized (mutex) {
			tempAssetId.set(fileName, fileType, assetType);
			return getLoadedOrLoad(tempAssetId, priority);
		}
	}

	public <T> T load(AssetId assetId, int priority) {
		synchronized (mutex) {
			return getLoadedOrLoad(assetId, priority);
		}
	}

	private <T> T getLoadedOrLoad(String fileName, FileType fileType, Class<T> assetType, int priority) {
		if (registry.isLoaded(fileName, fileType, assetType)) {
			return registry.get(assetId);
		}
		
		executor.load(assetId, priority);
		
		while (!registry.isLoaded(assetId)) {
			executor.update();
			ThreadUtils.yield();
		}
		
		return registry.get(assetId);
	}

	public boolean isLoaded(String fileName) {
		return registry.isLoaded(fileName);
	}

	public <T> boolean unload(T asset) {
		return registry.remove(asset);
	}

	public <T> T get(String fileName) {
		return registry.get(fileName);
	}

	public <T> T get(String fileName, String internalId) {
		return registry.get(fileName, internalId);
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

	public <T> void save(T asset) {
		persisters.save(asset);
	}

	public <T> void save(T asset, String fileName) {
		persisters.save(asset, fileName);
	}

	public <T> void save(T asset, String fileName, FileType fileType) {
		persisters.save(asset, fileName, fileType);
	}

	public <T> void save(FileHandle handle, T asset, boolean sticky) {
		persisters.persist(asset, handle);
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
