package com.gurella.engine.base.resource;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.asset.ConfigurableAssetDescriptor;

public class ResourceService {
	private static final ObjectMap<String, ConfigurableAssetDescriptor<?>> descriptors = new ObjectMap<String, ConfigurableAssetDescriptor<?>>();
	private static final AssetManager assetManager = new AssetManager();
	private static final ObjectIntMap<String> managedAssets = new ObjectIntMap<String>();

	private static final IntMap<String> objectsByFile = new IntMap<String>();

	private ResourceService() {
		Texture.setAssetManager(assetManager);
	}

	public static <T> ConfigurableAssetDescriptor<T> getAssetDescriptor(String fileName) {
		return null;
	}

	public static <T> void loadResourceAsync(String fileName, AsyncCallback<T> callback) {

	}

	public static <T> void loadResource(String fileName) {

	}

	public static boolean isResourceLoaded(String fileName) {
		return false;
	}

	public static <T> void loadResource(String fileName, Class<T> assetType, AsyncCallback<T> callback) {

	}

	public static <T> void loadResource(String fileName, Class<T> assetType) {

	}

	public static <T> void unloadResource(T resource) {

	}

	public static <T> void unloadUnusedResources() {

	}

	public static <T> Array<T> findResources(Class<T> resourceType, Array<T> out) {
		return out;
	}

	public static <T> String getResourceFileName(T resource) {
		if (resource instanceof ManagedObject) {
			synchronized (objectsByFile) {
				return objectsByFile.get(((ManagedObject) resource).instanceId);
			}
		} else {
			synchronized (assetManager) {
				return assetManager.getAssetFileName(resource);
			}
		}
	}

	public static boolean isResource(Object obj) {
		return getResourceFileName(obj) != null;
	}
}
