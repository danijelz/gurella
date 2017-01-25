package com.gurella.engine.asset;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationCleanupListener;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.utils.Values;
import com.gurella.engine.utils.priority.Priority;

//TODO add internal files cache???
@Priority(value = Integer.MIN_VALUE, type = ApplicationCleanupListener.class)
public final class AssetService implements ApplicationCleanupListener {
	private static final MockAssetManager mockManager = new MockAssetManager();
	
	private static final ObjectMap<Application, AssetService> instances = new ObjectMap<Application, AssetService>();
	private static AssetService lastSelected;
	private static Application lastApp;
	
	private final AssetRegistry assetRegistry = new AssetRegistry();
	private final ObjectMap<String, AssetConfig<?>> configs = new ObjectMap<String, AssetConfig<?>>();


	static {
		Texture.setAssetManager(mockManager);
		Cubemap.setAssetManager(mockManager);
	}

	private AssetService() {
	}

	@Override
	public void cleanup() {
		assetRegistry.update();
	}

	private static AssetService getInstance() {
		AssetService service;
		boolean subscribe = false;

		synchronized (instances) {
			Application app = Gdx.app;
			if (lastApp == app) {
				return lastSelected;
			}

			service = instances.get(app);
			if (service == null) {
				service = new AssetService();
				instances.put(app, service);
				subscribe = true;
			}

			lastApp = app;
			lastSelected = service;

		}

		if (subscribe) {
			EventService.subscribe(new Cleaner());
			EventService.subscribe(service);
		}

		return service;
	}

	// TODO unused -> should be managed internaly by loader task
	public static <T> AssetConfig<T> getAssetConfig(String fileName) {
		return Values.cast(getInstance().configs.get(fileName));
	}

	// TODO unused
	private static <T> AssetLoaderParameters<T> getAssetLoaderParameters(String fileName) {
		AssetConfig<T> descriptor = Values.cast(getInstance().configs.get(fileName));
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

	public static <T> boolean unload(T asset) {
		return getInstance().assetRegistry.unload(asset);
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

	public static <T> String getFileName(T asset) {
		return getInstance().assetRegistry.getAssetFileName(asset);
	}

	public static boolean isManaged(Object asset) {
		return getInstance().assetRegistry.containsAsset(asset);
	}

	public static boolean isManaged(String fileName) {
		return getInstance().assetRegistry.isLoaded(fileName);
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

	public static <T> void save(T asset) {
		getInstance().assetRegistry.save(asset);
	}

	public static <T> void save(T asset, String fileName) {
		getInstance().assetRegistry.save(asset, fileName);
	}

	public static <T> void save(T asset, String fileName, FileType fileType) {
		getInstance().assetRegistry.save(asset, fileName, fileType);
	}

	public static <T> void save(T asset, FileHandle handle) {
		getInstance().assetRegistry.save(asset, handle);
	}

	public static void delete(String fileName) {
		getInstance().assetRegistry.delete(fileName);
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
			AssetService removed;

			synchronized (instances) {
				removed = instances.remove(Gdx.app);

				if (removed == lastSelected) {
					lastSelected = null;
					lastApp = null;
				}
			}

			EventService.unsubscribe(removed);
			removed.assetRegistry.dispose();
		}
	}
}
