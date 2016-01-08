package com.gurella.engine.asset;

import com.badlogic.gdx.utils.ObjectMap;

//TODO unused
public class AssetService {
	private static final ObjectMap<String, ConfigurableAssetDescriptor<?>> assetDescriptors = new ObjectMap<String, ConfigurableAssetDescriptor<?>>();
	private static final AssetRegistry assetRegistry = new AssetRegistry();
	
	private AssetService() {
	}
	
	
	
	public static <T> ConfigurableAssetDescriptor<T> getAssetDescriptor(String filePath) {
		
	}
}
