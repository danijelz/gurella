package com.gurella.engine.asset;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.utils.Values;
import com.gurella.engine.utils.priority.TypePriorities;
import com.gurella.engine.utils.priority.TypePriority;

public final class AssetService {
	private static final ObjectMap<String, ConfigurableAssetDescriptor<?>> descriptors = new ObjectMap<String, ConfigurableAssetDescriptor<?>>();

	private static final MockAssetManager mockManager = new MockAssetManager();
	private static final AssetRegistry assetRegistry = new AssetRegistry();
	private static final RegistryUpdater updateListener = new RegistryUpdater();

	static {
		Texture.setAssetManager(mockManager);
		Cubemap.setAssetManager(mockManager);
		EventService.subscribe(updateListener);
	}

	private AssetService() {
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
		AssetLoaderParameters<T> parameters = AssetService.<T> getAssetLoaderParameters(fileName);
		assetRegistry.load(fileName, type, parameters, callback, priority, sticky);
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
		AssetLoaderParameters<T> parameters = AssetService.<T> getAssetLoaderParameters(fileName);
		assetRegistry.load(fileName, type, parameters, null, priority, sticky);
		return assetRegistry.finishLoading(fileName);
	}

	public static boolean isLoaded(String fileName) {
		return assetRegistry.isLoaded(fileName);
	}

	public static <T> boolean unload(T resource) {
		return assetRegistry.unload(resource);
	}

	public static <T> T get(String fileName) {
		return assetRegistry.get(fileName);
	}

	//TODO replace with save(T asset, String fileName, AssetPersister persister)
	public static <T> void put(T asset, String fileName) {
		assetRegistry.put(asset, fileName);
	}

	public static <T> Array<T> find(Class<T> type, Array<T> out) {
		// TODO ManagedObject
		return assetRegistry.getAll(type, out);
	}

	public static <T> String getFileName(T resource) {
		synchronized (assetRegistry) {
			return assetRegistry.getAssetFileName(resource);
		}
	}

	public static boolean isManaged(Object obj) {
		return getFileName(obj) != null;
	}

	public static <T> T reload(String fileName, int priority) {
		assetRegistry.reload(fileName, null, priority);
		return assetRegistry.finishLoading(fileName);
	}

	public static <T> void reloadAsync(String fileName, AsyncCallback<T> callback, int priority) {
		assetRegistry.reload(fileName, callback, priority);
	}

	public static void reloadInvalidated() {
		assetRegistry.reloadInvalidated();
	}

	@TypePriorities({ @TypePriority(priority = CommonUpdatePriority.ioPriority, type = ApplicationUpdateListener.class),
			@TypePriority(priority = CommonUpdatePriority.ioPriority, type = ApplicationDebugUpdateListener.class) })
	private static class RegistryUpdater implements ApplicationUpdateListener, ApplicationDebugUpdateListener {
		@Override
		public void update() {
			assetRegistry.update();
		}

		@Override
		public void debugUpdate() {
			assetRegistry.update();
		}
	}
}
