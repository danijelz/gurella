package com.gurella.engine.asset2.loader;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset2.registry.AssetRegistry;
import com.gurella.engine.async.AsyncCallback;

public class AssetLoaders {
	private final AssetRegistry registry;

	public AssetLoaders(AssetRegistry registry) {
		this.registry = registry;
	}

	public <T> void loadAsync(String fileName, FileType fileType, Class<T> assetType, AsyncCallback<T> callback,
			int priority, boolean sticky) {
		AssetLoaderParameters<T> parameters = AssetService.<T> getAssetLoaderParameters(fileName);
		getInstance().assetRegistry.load(fileName, assetType, parameters, callback, priority, sticky);
	}

	public <T> T load(String fileName, FileType fileType, Class<T> assetType, int priority, boolean sticky) {
		AssetLoaderParameters<T> parameters = AssetService.<T> getAssetLoaderParameters(fileName);
		AssetRegistry assetRegistry = getInstance().assetRegistry;
		assetRegistry.load(fileName, assetType, parameters, null, priority, sticky);
		return assetRegistry.finishLoading(fileName);
	}
}
