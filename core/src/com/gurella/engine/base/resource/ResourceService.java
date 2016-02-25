package com.gurella.engine.base.resource;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.application.events.ApplicationUpdateSignal.ApplicationUpdateListener;
import com.gurella.engine.application.events.CommonUpdatePriority;
import com.gurella.engine.asset.AssetDatabase;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.asset.ConfigurableAssetDescriptor;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.Signal1;
import com.gurella.engine.utils.Values;

public class ResourceService implements ApplicationUpdateListener {
	private static final ObjectMap<String, ConfigurableAssetDescriptor<?>> descriptors = new ObjectMap<String, ConfigurableAssetDescriptor<?>>();

	private static final MockAssetManager mockManager = new MockAssetManager();

	private static final AssetDatabase assetDatabase = new AssetDatabase();
	private static final IntMap<String> objectsByFile = new IntMap<String>();

	private static final Signal1<String> resourceLoadedSignal = new Signal1<String>();
	private static final Signal1<String> resourceUnloadedSignal = new Signal1<String>();
	private static final Signal1<String> resourceRefreshSignal = new Signal1<String>();

	static {
		Texture.setAssetManager(mockManager);
		Cubemap.setAssetManager(mockManager);
	}

	private ResourceService() {
	}

	public static <T> ConfigurableAssetDescriptor<T> getAssetDescriptor(String fileName) {
		return Values.cast(descriptors.get(fileName));
	}

	public static <T> AssetLoaderParameters<T> getAssetLoaderParameters(String fileName) {
		ConfigurableAssetDescriptor<T> descriptor = Values.cast(descriptors.get(fileName));
		return descriptor == null ? null : descriptor.getParameters();
	}

	public static <T> void loadAsync(String fileName, AsyncCallback<T> callback, int priority) {
		Class<T> type = getResourceType(fileName);
		loadAsync(fileName, type, callback, priority, false);
	}

	private static <T> Class<T> getResourceType(String fileName) {
		Class<T> type = Assets.getAssetType(fileName);
		if (type == null) {
			throw new GdxRuntimeException("Can't find resource type from file name:" + fileName);
		}
		return type;
	}

	public static <T> void loadAsync(String fileName, Class<T> type, AsyncCallback<T> callback, int priority) {
		loadAsync(fileName, type, callback, priority, false);
	}

	public static <T> void loadAsync(String fileName, Class<T> type, AsyncCallback<T> callback, int priority,
			boolean sticky) {
		AssetLoaderParameters<T> parameters = ResourceService.<T> getAssetLoaderParameters(fileName);
		assetDatabase.load(fileName, type, parameters, callback, priority, sticky);
	}

	public static <T> T load(String fileName) {
		Class<T> type = getResourceType(fileName);
		return load(fileName, type, 0, false);
	}

	public static <T> T load(String fileName, Class<T> type) {
		return load(fileName, type, 0, false);
	}

	public static <T> T load(String fileName, Class<T> type, int priority) {
		return load(fileName, type, priority, false);
	}

	public static <T> T load(String fileName, Class<T> type, int priority, boolean sticky) {
		AssetLoaderParameters<T> parameters = ResourceService.<T> getAssetLoaderParameters(fileName);
		assetDatabase.load(fileName, type, parameters, null, priority, sticky);
		return assetDatabase.finishLoading(fileName);
	}

	public static boolean isLoaded(String fileName) {
		return assetDatabase.isLoaded(fileName);
	}

	public static <T> void unload(T resource) {
		assetDatabase.unload(resource);
	}

	public static <T> Array<T> find(Class<T> type, Array<T> out) {
		// TODO ManagedObject
		return assetDatabase.getAll(type, out);
	}

	public static <T> String getFileName(T resource) {
		if (resource instanceof ManagedObject) {
			synchronized (objectsByFile) {
				return objectsByFile.get(((ManagedObject) resource).getInstanceId());
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

	public static <T> T reload(String fileName, int priority) {
		assetDatabase.reload(fileName, null, priority);
		return assetDatabase.finishLoading(fileName);
	}

	public static <T> void reloadAsync(String fileName, AsyncCallback<T> callback, int priority) {
		assetDatabase.reload(fileName, callback, priority);
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
