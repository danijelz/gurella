package com.gurella.engine.asset2;

import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.persister.AssetPersister;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.plugin.Plugin;

//TODO unused, should replace AssetType
public class AssetDescriptor<TYPE, PROPS extends AssetProperties<TYPE>> implements Plugin {
	public final Class<TYPE> assetType = null;
	public final String[] fileExtensions = null;
	public final boolean composite = false;
	public final Class<PROPS> propertiesType = null;
	public final AssetLoader<?, TYPE, PROPS> loader = null;
	public final AssetPersister<TYPE> persister = null;
	//TODO AssetReloader
}
