package com.gurella.engine.asset;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.utils.Values;
import com.gurella.engine.utils.priority.Priorities;
import com.gurella.engine.utils.priority.Priority;

@Priorities({ @Priority(value = CommonUpdatePriority.ioPriority, type = ApplicationUpdateListener.class),
		@Priority(value = CommonUpdatePriority.ioPriority, type = ApplicationDebugUpdateListener.class) })
public final class AssetService implements ApplicationUpdateListener, ApplicationDebugUpdateListener {
	private static final ObjectMap<Application, AssetService> instances = new ObjectMap<Application, AssetService>();
	private static final MockAssetManager mockManager = new MockAssetManager();

	private final AssetRegistry assetRegistry = new AssetRegistry();
	private final ObjectMap<String, ConfigurableAssetDescriptor<?>> descriptors = new ObjectMap<String, ConfigurableAssetDescriptor<?>>();

	static {
		Texture.setAssetManager(mockManager);
		Cubemap.setAssetManager(mockManager);
	}

	private AssetService() {
	}

	@Override
	public void update() {
		assetRegistry.update();
	}

	@Override
	public void debugUpdate() {
		assetRegistry.update();
	}

	private static AssetService getInstance() {
		synchronized (instances) {
			AssetService service = instances.get(Gdx.app);
			if (service == null) {
				service = new AssetService();
				instances.put(Gdx.app, service);
				EventService.subscribe(service);
				EventService.subscribe(new Cleaner());
			}
			return service;
		}
	}

	public static <T> ConfigurableAssetDescriptor<T> getAssetDescriptor(String fileName) {
		return Values.cast(getInstance().descriptors.get(fileName));
	}

	public static <T> AssetLoaderParameters<T> getAssetLoaderParameters(String fileName) {
		ConfigurableAssetDescriptor<T> descriptor = Values.cast(getInstance().descriptors.get(fileName));
		return descriptor == null ? null : descriptor.getParameters();
	}

	public static <T> void loadAsync(String fileName, AsyncCallback<T> callback, int priority) {
		Class<T> type = getAssetType(fileName);
		loadAsync(fileName, type, callback, priority, false);
	}

	private static <T> Class<T> getAssetType(String fileName) {
		Class<T> type = Assets.getAssetClass(fileName);
		if (type == null) {
			throw new GdxRuntimeException("Can't extract asset class from file name: " + fileName);
		}
		return type;
	}

	public static <T> void loadAsync(String fileName, Class<T> type, AsyncCallback<T> callback, int priority) {
		loadAsync(fileName, type, callback, priority, false);
	}

	public static <T> void loadAsync(String fileName, Class<T> type, AsyncCallback<T> callback, int priority,
			boolean sticky) {
		AssetLoaderParameters<T> parameters = AssetService.<T> getAssetLoaderParameters(fileName);
		getInstance().assetRegistry.load(fileName, type, parameters, callback, priority, sticky);
	}

	public static <T> T load(String fileName) {
		Class<T> type = getAssetType(fileName);
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
		AssetRegistry assetRegistry = getInstance().assetRegistry;
		assetRegistry.load(fileName, type, parameters, null, priority, sticky);
		return assetRegistry.finishLoading(fileName);
	}

	public static boolean isLoaded(String fileName) {
		return getInstance().assetRegistry.isLoaded(fileName);
	}

	public static <T> boolean unload(T resource) {
		return getInstance().assetRegistry.unload(resource);
	}

	public static <T> T get(String fileName) {
		return getInstance().assetRegistry.get(fileName);
	}

	public static <T> T get(String fileName, String internalId) {
		return getInstance().assetRegistry.get(fileName, internalId);
	}

	public static <T> Array<T> find(Class<T> type, Array<T> out) {
		return getInstance().assetRegistry.getAll(type, out);
	}

	public static <T> String getFileName(T resource) {
		return getInstance().assetRegistry.getAssetFileName(resource);
	}

	public static boolean isManaged(Object asset) {
		return getInstance().assetRegistry.containsAsset(asset);
	}

	public static <T> T reload(String fileName, int priority) {
		AssetRegistry assetRegistry = getInstance().assetRegistry;
		assetRegistry.reload(fileName, null, priority);
		return assetRegistry.finishLoading(fileName);
	}

	public static <T> void reloadAsync(String fileName, AsyncCallback<T> callback, int priority) {
		getInstance().assetRegistry.reload(fileName, callback, priority);
	}

	public static void reloadInvalidated() {
		getInstance().assetRegistry.reloadInvalidated();
	}

	// TODO replace with save(T asset, String fileName) {
	// AssetPersister persister ...
	// public static <T extends ManagedObject> void save(T object, Class<? super T> expectedType, String fileName) {
	// FileHandle handle = Gdx.files.local(fileName);
	// if (handle.exists()) {
	// // TODO exception
	// }
	//
	// JsonOutput output = new JsonOutput();
	// String string = output.serialize(handle, expectedType, object);
	// OutputStream outputStream = handle.write(false);
	//
	// try {
	// outputStream.write(new JsonReader().parse(string).prettyPrint(OutputType.minimal, 120).getBytes());
	// outputStream.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// AssetService.put(object, fileName);
	// }
	// }
	public static <T> void put(T asset, String fileName) {
		getInstance().assetRegistry.put(asset, fileName);
	}

	public static void addDependency(Object asset, Object dependency) {
		getInstance().assetRegistry.addDependency(asset, dependency);
	}

	public static void removeDependency(Object asset, Object dependency) {
		getInstance().assetRegistry.removeDependency(asset, dependency);
	}

	public static void replaceDependency(Object asset, Object oldDependency, Object newDependency) {
		getInstance().assetRegistry.replaceDependency(asset, oldDependency, newDependency);
	}

	public static void addToBundle(Bundle bundle, String internalId, Object asset) {
		getInstance().assetRegistry.addToBundle(bundle, internalId, asset);
	}

	public static void removeFromBundle(Bundle bundle, String internalId, Object asset) {
		getInstance().assetRegistry.removeFromBundle(bundle, internalId, asset);
	}

	public static String getBundledAssetInternalId(Object asset) {
		return getInstance().assetRegistry.getBundledAssetInternalId(asset);
	}

	private static class Cleaner implements ApplicationShutdownListener {
		@Override
		public void shutdown() {
			EventService.unsubscribe(this);
			AssetService service;
			synchronized (instances) {
				service = instances.remove(Gdx.app);
			}

			if (service != null) {
				EventService.unsubscribe(service);
				service.assetRegistry.dispose();
			}
		}
	}
}
