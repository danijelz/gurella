package com.gurella.engine.base.resource;

import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.application.events.CommonUpdatePriority;
import com.gurella.engine.application.events.UpdateListener;
import com.gurella.engine.asset.ConfigurableAssetDescriptor;
import com.gurella.engine.asset.manager.AssetManager;
import com.gurella.engine.event.Signal1.Signal1Impl;

public class ResourceService implements UpdateListener {
	private static final ObjectMap<String, ConfigurableAssetDescriptor<?>> descriptors = new ObjectMap<String, ConfigurableAssetDescriptor<?>>();
	private static final AssetManager assetManager = new AssetManager();
	private static final IntMap<String> objectsByFile = new IntMap<String>();
	
	private static final Signal1Impl<String> resourceLoadedSignal = new Signal1Impl<String>();
	private static final Signal1Impl<String> resourceUnloadedSignal = new Signal1Impl<String>();
	
	static {
		//TODO reset must be handled by application -> TextureParameter.textureData.
		Texture.setAssetManager(assetManager);
		Cubemap.setAssetManager(assetManager);
	}

	private ResourceService() {
	}

	public static <T> ConfigurableAssetDescriptor<T> getAssetDescriptor(String fileName) {
		return null;
	}

	public static <T> void loadResourceAsync(String fileName, AsyncCallback<T> callback, int priority) {

	}

	public static <T> void loadResourceAsync(String fileName, Class<T> type, AsyncCallback<T> callback, int priority) {

	}

	public static <T> void loadResource(String fileName) {

	}

	public static <T> void loadResource(String fileName, Class<T> type) {

	}

	public static boolean isResourceLoaded(String fileName) {
		return false;
	}

	public static <T> void unloadResource(T resource) {

	}

	public static void unloadUnusedResources() {

	}

	public static <T> Array<T> findResources(Class<T> type, Array<T> out) {
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

	public static boolean isManagedResource(Object obj) {
		return getResourceFileName(obj) != null;
	}

	@Override
	public int getPriority() {
		return CommonUpdatePriority.LOAD;
	}

	@Override
	public void update() {
		assetManager.update();
	}
}
