package com.gurella.engine.base.resource;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.asset.AssetRegistry;
import com.gurella.engine.asset.ConfigurableAssetDescriptor;
import com.gurella.engine.base.registry.AsyncCallback;

public class ResourceService {
	private static final ObjectMap<String, ConfigurableAssetDescriptor<?>> descriptors = new ObjectMap<String, ConfigurableAssetDescriptor<?>>();
	private static final AssetRegistry registry = new AssetRegistry();

	private ResourceService() {
	}

	public static <T> ConfigurableAssetDescriptor<T> getAssetDescriptor(String fileName) {

	}

	public static <T> void loadResource(String fileName, AsyncCallback<T> callback) {

	}

	public static <T> void loadResource(String fileName) {

	}

	public static <T> void loadResource(String fileName, Class<T> assetType, AsyncCallback<T> callback) {

	}

	public static <T> void loadResource(String fileName, Class<T> assetType) {

	}

	public static <T> void unloadResource(T resource) {

	}

	public static <T> void unloadUnusedResources() {

	}

	public static <T> void findResources(Class<T> type) {

	}
}
