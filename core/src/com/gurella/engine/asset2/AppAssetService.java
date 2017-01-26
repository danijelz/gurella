package com.gurella.engine.asset2;

import com.gurella.engine.asset2.loader.AssetLoaders;
import com.gurella.engine.asset2.persister.AssetPersisters;
import com.gurella.engine.asset2.registry.AssetRegistry;

public class AppAssetService {
	private final AssetRegistry registry = new AssetRegistry();
	private final AssetLoaders loaders = new AssetLoaders(registry);
	private final AssetPersisters persisters = new AssetPersisters(registry);
}
