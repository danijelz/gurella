package com.gurella.engine.asset2;

import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.persister.AssetPersister;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.plugin.Plugin;
import com.gurella.engine.utils.ArrayExt;

//TODO unused, should replace AssetType
public class AssetDescriptor<TYPE> implements Plugin {
	public final Class<TYPE> assetType = null;
	public final boolean extensible = true;
	public final boolean composite = false;

	private final ArrayExt<AssetLoaderConfig<TYPE>> loaders = null;
	private final ArrayExt<AssetPersisterConfig<TYPE>> persisters = null;

	public final AssetPersister<TYPE> persister = null;
	// TODO AssetReloader, MissingValueProvider

	public static class AssetLoaderConfig<TYPE> {
		public final String[] fileExtensions = null;
		public final AssetLoader<?, TYPE, ? extends AssetProperties<TYPE>> loader = null;
	}

	public static class AssetPersisterConfig<TYPE> {
		public final String[] fileExtensions = null;
		public final AssetPersister<TYPE> persister = null;
	}
}
