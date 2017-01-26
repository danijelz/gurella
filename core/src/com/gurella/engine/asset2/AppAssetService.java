package com.gurella.engine.asset2;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.asset.AssetConfig;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.asset2.loader.AssetLoaders;
import com.gurella.engine.asset2.persister.AssetPersisters;
import com.gurella.engine.asset2.registry.AssetRegistry;
import com.gurella.engine.async.AsyncCallback;

public class AppAssetService {
	private final AssetRegistry registry = new AssetRegistry();
	private final AssetLoaders loaders = new AssetLoaders(registry);
	private final AssetPersisters persisters = new AssetPersisters(registry);

	//TODO 
	public <T> AssetConfig<T> getAssetConfig(String fileName) {
		return null;
	}

	// TODO unused
	private <T> AssetLoaderParameters<T> getAssetLoaderParameters(String fileName) {
		AssetConfig<T> descriptor = null;
		return descriptor == null ? null : descriptor.getParameters();
	}

	public <T> void loadAsync(String fileName, AsyncCallback<T> callback) {
		Class<T> type = getAssetType(fileName);
		loadAsync(fileName, type, callback, priority, false);
	}

	public <T> void loadAsync(String fileName, AsyncCallback<T> callback, int priority) {
		Class<T> type = getAssetType(fileName);
		loadAsync(fileName, type, callback, priority, false);
	}

	public <T> void loadAsync(String fileName, Class<T> assetType, AsyncCallback<T> callback, int priority) {
		loadAsync(fileName, type, callback, priority, false);
	}

	public <T> void loadAsync(String fileName, Class<T> assetType, AsyncCallback<T> callback, int priority,
			boolean sticky) {
		AssetLoaderParameters<T> parameters = AssetService.<T> getAssetLoaderParameters(fileName);
		getInstance().assetRegistry.load(fileName, type, parameters, callback, priority, sticky);
	}

	public <T> T load(String fileName) {
		Class<T> type = getAssetType(fileName);
		return load(fileName, type, 0, false);
	}

	public <T> T load(String fileName, Class<T> assetType) {
		return load(fileName, type, 0, false);
	}

	public <T> T load(String fileName, Class<T> assetType, int priority) {
		return load(fileName, type, priority, false);
	}

	public <T> T load(String fileName, FileType fileType, Class<T> assetType, int priority, boolean sticky) {
		AssetLoaderParameters<T> parameters = AssetService.<T> getAssetLoaderParameters(fileName);
		AssetRegistry assetRegistry = getInstance().assetRegistry;
		assetRegistry.load(fileName, type, parameters, null, priority, sticky);
		return assetRegistry.finishLoading(fileName);
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
		return registry.getAssetFileName(asset);
	}

	public boolean isManaged(Object asset) {
		return registry.containsAsset(asset);
	}

	public boolean isManaged(String fileName) {
		return registry.isLoaded(fileName);
	}

	public <T> void save(T asset) {
		persisters.save(asset);
	}

	public <T> void save(T asset, String fileName) {
		getInstance().assetRegistry.save(asset, fileName);
	}

	public <T> void save(T asset, String fileName, FileType fileType) {
		getInstance().assetRegistry.save(asset, fileName, fileType);
	}

	public <T> void save(T asset, FileHandle handle) {
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

	public String getBundledAssetInternalId(Object asset) {
		return registry.getBundleId(asset);
	}
}
