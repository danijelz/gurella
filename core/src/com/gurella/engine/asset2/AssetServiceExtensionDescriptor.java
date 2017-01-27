package com.gurella.engine.asset2;

import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.persister.AssetPersister;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.plugin.Plugin;

//TODO unused, should replace AssetType
public class AssetServiceExtensionDescriptor<T> implements Plugin {
	public final Class<T> assetType = null;
	public final String[] fileExtensions = null;
	public final boolean composite = false;
	public final Class<? extends AssetProperties<T>> propertiesType = null;
	public final AssetLoader<?, T, AssetProperties<T>> loader = null;
	public final AssetPersister<T> persister = null;
}
