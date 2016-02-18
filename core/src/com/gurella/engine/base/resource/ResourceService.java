package com.gurella.engine.base.resource;

import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.application.events.CommonUpdatePriority;
import com.gurella.engine.application.events.UpdateListener;
import com.gurella.engine.asset.ConfigurableAssetDescriptor;
import com.gurella.engine.asset.manager.AssetDatabase;
import com.gurella.engine.event.Signal1.Signal1Impl;
import com.gurella.engine.utils.ValueUtils;

public class ResourceService implements UpdateListener {
	private static final ObjectMap<String, ConfigurableAssetDescriptor<?>> descriptors = new ObjectMap<String, ConfigurableAssetDescriptor<?>>();

	private static final MockAssetManager mockManager = new MockAssetManager();

	private static final AssetDatabase assetDatabase = new AssetDatabase();
	private static final IntMap<String> objectsByFile = new IntMap<String>();

	private static final Signal1Impl<String> resourceLoadedSignal = new Signal1Impl<String>();
	private static final Signal1Impl<String> resourceUnloadedSignal = new Signal1Impl<String>();

	static {
		Texture.setAssetManager(mockManager);
		Cubemap.setAssetManager(mockManager);
	}

	private ResourceService() {
	}

	public static <T> ConfigurableAssetDescriptor<T> getAssetDescriptor(String fileName) {
		return ValueUtils.cast(descriptors.get(fileName));
	}

	public static <T> void loadAsync(String fileName, AsyncCallback<T> callback, int priority) {

	}

	public static <T> void loadAsync(String fileName, Class<T> type, AsyncCallback<T> callback, int priority) {

	}

	public static <T> void load(String fileName) {

	}

	public static <T> void load(String fileName, Class<T> type) {

	}

	public static boolean isLoaded(String fileName) {
		return assetDatabase.isLoaded(fileName);
	}

	public static <T> void unload(T resource) {
		assetDatabase.unload(resource);
	}

	public static <T> Array<T> find(Class<T> type, Array<T> out) {
		return out;
	}

	public static <T> String getFileName(T resource) {
		if (resource instanceof ManagedObject) {
			synchronized (objectsByFile) {
				return objectsByFile.get(((ManagedObject) resource).instanceId);
			}
		} else {
			synchronized (assetDatabase) {
				return assetDatabase.getAssetFileName(resource);
			}
		}
	}

	public static boolean isManaged(Object obj) {
		return getFileName(obj) != null;
	}

	public static void reload(String fileName, int priority) {
		assetDatabase.reload(fileName, priority);
	}

	public static void reloadInvalidated() {
		assetDatabase.reloadInvalidated();
	}

	@Override
	public int getPriority() {
		return CommonUpdatePriority.LOAD;
	}

	@Override
	public void update() {
		assetDatabase.update();
	}
}
