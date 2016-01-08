package com.gurella.engine.asset;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.base.registry.AsyncCallback;

//TODO unused
public class AssetService {
	private static final ObjectMap<String, ConfigurableAssetDescriptor<?>> descriptors = new ObjectMap<String, ConfigurableAssetDescriptor<?>>();
	private static final AssetRegistry registry = new AssetRegistry();
	
	private AssetService() {
	}
	
	public static <T> ConfigurableAssetDescriptor<T> getAssetDescriptor(String filePath) {
		
	}
	
	public static <T> void loadAsset(String filePath, AsyncCallback<T> callback) {
		
	}
}
