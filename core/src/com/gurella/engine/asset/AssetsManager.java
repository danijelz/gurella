package com.gurella.engine.asset;

import static com.gurella.engine.asset.AssetLoadingPhase.finished;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.asset.bundle.Bundle;
import com.gurella.engine.asset.descriptor.AssetDescriptors;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.asset.persister.AssetPersister;
import com.gurella.engine.asset.persister.DependencyLocator;
import com.gurella.engine.asset.resolver.FileHandleResolver;
import com.gurella.engine.asset.resolver.FilehandleResolverRegistry;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.async.SimpleAsyncCallback;
import com.gurella.engine.subscriptions.application.ApplicationCleanupListener;
import com.gurella.engine.utils.factory.Factory;
import com.gurella.engine.utils.priority.Priority;

@Priority(value = Integer.MIN_VALUE, type = ApplicationCleanupListener.class)
class AssetsManager implements ApplicationCleanupListener, DependencyLocator, Disposable {
	final Object mutex = new Object();
	AssetLoadingExecutor executor = new AssetLoadingExecutor(this);
	private final TaskPool taskPool = new TaskPool();

	private final Files files = Gdx.files;
	private final AssetRegistry registry = new AssetRegistry();

	private final FilehandleResolverRegistry resolvers = new FilehandleResolverRegistry();

	private final IdentityMap<Factory<? extends AssetLoader<?, ?>>, AssetLoader<?, ?>> loaders = new IdentityMap<Factory<? extends AssetLoader<?, ?>>, AssetLoader<?, ?>>();
	private final IdentityMap<Factory<? extends AssetPersister<?>>, AssetPersister<?>> persisters = new IdentityMap<Factory<? extends AssetPersister<?>>, AssetPersister<?>>();

	private final AssetId tempAssetId = new AssetId();

	boolean isLoaded(String fileName, FileType fileType) {
		return registry.isLoaded(tempAssetId.set(fileName, fileType, AssetDescriptors.getAssetType(fileName)));
	}

	boolean isLoaded(String fileName, FileType fileType, Class<?> assetType) {
		synchronized (mutex) {
			return registry.isLoaded(tempAssetId.set(fileName, fileType, assetType));
		}
	}

	<T> UnloadResult unload(T asset) {
		synchronized (mutex) {
			return registry.unload(asset);
		}
	}

	<T> Array<T> getAll(Class<T> type, Array<T> out) {
		synchronized (mutex) {
			return registry.getAll(type, out);
		}
	}

	<T> String getFileName(T asset) {
		synchronized (mutex) {
			return registry.getFileName(asset);
		}
	}

	<T> FileType getFileType(T asset) {
		synchronized (mutex) {
			return registry.getFileType(asset);
		}
	}

	@Override
	public AssetId getAssetId(Object asset, AssetId out) {
		synchronized (mutex) {
			return registry.getAssetId(asset, out);
		}
	}

	boolean isManaged(Object asset) {
		return registry.isManaged(asset);
	}

	<T> void save(T asset) {
		synchronized (mutex) {
			if (registry.getAssetId(asset, tempAssetId).isEmpty()) {
				throw new IllegalStateException("Asset is not yet persisted.");
			}

			String fileName = tempAssetId.fileName;
			@SuppressWarnings("unchecked")
			Class<T> assetType = (Class<T>) tempAssetId.assetType;
			AssetPersister<T> persister = getPersister(fileName, assetType);
			FileHandle file = resolvers.resolveFile(tempAssetId);
			persister.persist(this, file, asset);
		}
	}

	private <T> AssetPersister<T> getPersister(String fileName, Class<T> assetType) {
		Factory<AssetPersister<T>> factory = AssetDescriptors.getPersisterFactory(fileName, assetType);
		if (factory == null) {
			throw new IllegalStateException("No persistor registered for asset type: " + assetType.getName());
		}

		@SuppressWarnings("unchecked")
		AssetPersister<T> persister = (AssetPersister<T>) persisters.get(factory);
		if (persister == null) {
			persister = factory.create();
			persisters.put(factory, persister);
		}

		return persister;
	}

	<T> void save(T asset, String fileName, FileType fileType) {
		synchronized (mutex) {
			Class<T> assetType = AssetDescriptors.getAssetType(asset);
			if (assetType == null) {
				throw new IllegalStateException(
						"No descrptor registered for asset type: " + asset.getClass().getName());
			}

			AssetPersister<T> persister = getPersister(fileName, assetType);
			if (registry.getAssetId(asset, tempAssetId).isEmpty()) {
				FileHandle file = files.getFileHandle(fileName, fileType);
				persister.persist(this, file, asset);
				tempAssetId.set(file, assetType);
				registry.add(tempAssetId, asset);
			} else if (tempAssetId.equalsFile(fileName, fileType)) {
				FileHandle file = files.getFileHandle(fileName, fileType);
				persister.persist(this, file, asset);
			} else {
				throw new IllegalStateException("Asset allready persisted on another location.");
			}
		}
	}

	DeleteResult delete(Object asset) {
		synchronized (mutex) {
			if (registry.getAssetId(asset, tempAssetId).isEmpty()) {
				return DeleteResult.unexisting;
			}

			String fileName = tempAssetId.fileName;
			FileType fileType = tempAssetId.fileType;
			registry.removeAll(fileName, fileType);

			FileHandle file = resolvers.resolveFile(tempAssetId);
			if (!file.exists()) {
				return DeleteResult.unexisting;
			} else if (file.delete()) {
				return DeleteResult.deleted;
			} else {
				return DeleteResult.unsuccessful;
			}
		}
	}

	DeleteResult delete(String fileName, FileType fileType) {
		synchronized (mutex) {
			registry.removeAll(fileName, fileType);
			FileHandle file = files.getFileHandle(fileName, fileType);
			if (!file.exists()) {
				return DeleteResult.unexisting;
			} else if (file.delete()) {
				return DeleteResult.deleted;
			} else {
				return DeleteResult.unsuccessful;
			}
		}
	}

	// TODO dependencies and bundle contents should be handled diferently
	void addDependency(Object asset, Object dependency) {
		synchronized (mutex) {
			registry.addDependency(asset, dependency);
		}
	}

	void removeDependency(Object asset, Object dependency) {
		synchronized (mutex) {
			registry.removeDependency(asset, dependency);
		}
	}

	void replaceDependency(Object asset, Object oldDependency, Object newDependency) {
		synchronized (mutex) {
			registry.replaceDependency(asset, oldDependency, newDependency);
		}
	}

	void addToBundle(Bundle bundle, Object asset, String internalId) {
		synchronized (mutex) {
			registry.addToBundle(bundle, asset, internalId);
		}
	}

	void removeFromBundle(Bundle bundle, Object asset) {
		synchronized (mutex) {
			registry.removeFromBundle(bundle, asset);
		}
	}

	String getBundleId(Object asset) {
		synchronized (mutex) {
			return registry.getBundleId(asset);
		}
	}

	Bundle getBundle(Object asset) {
		synchronized (mutex) {
			return registry.getAssetRootBundle(asset);
		}
	}

	////////////////////////////// loading

	<T> void loadAsync(AsyncCallback<? super T> callback, String fileName, FileType fileType, int priority) {
		loadAsync(callback, fileName, fileType, AssetDescriptors.<T> getAssetType(fileName), priority);
	}

	<T> void loadAsync(AsyncCallback<? super T> callback, String fileName, FileType fileType, Class<T> assetType,
			int priority) {
		synchronized (mutex) {
			T asset = registry.getLoaded(tempAssetId.set(fileName, fileType, assetType), null);
			if (asset == null) {
				load(callback, fileName, fileType, assetType, priority);
			} else {
				callback.onProgress(1f);
				callback.onSuccess(asset);
			}
		}
	}

	<T> T load(String fileName, FileType fileType) {
		return load(fileName, fileType, AssetDescriptors.<T> getAssetType(fileName));
	}

	<T> T load(String fileName, FileType fileType, Class<T> assetType) {
		SimpleAsyncCallback<T> callback;
		synchronized (mutex) {
			T asset = registry.getLoaded(tempAssetId.set(fileName, fileType, assetType), null);
			if (asset != null) {
				return asset;
			}

			callback = SimpleAsyncCallback.obtain();
			load(callback, fileName, fileType, assetType, Integer.MAX_VALUE);
		}

		while (!callback.isDone()) {
			executor.update();
			ThreadUtils.yield();
		}

		if (callback.isFailed()) {
			tempAssetId.set(fileName, fileType, assetType);
			throw new RuntimeException("Error loading asset " + tempAssetId, callback.getExceptionAndFree());
		} else {
			return callback.getValueAndFree();
		}
	}

	private <T> void load(AsyncCallback<? super T> callback, String fileName, FileType fileType, Class<T> assetType,
			int priority) {
		tempAssetId.set(fileName, fileType, assetType);
		AssetLoadingTask<T> queuedTask = executor.findTask(tempAssetId);

		if (queuedTask == null) {
			AssetLoadingTask<T> task = taskPool.obtainTask();
			FileHandle file = resolvers.resolveFile(tempAssetId);
			AssetLoader<T, AssetProperties> loader = getLoader(fileName, assetType);
			task.init(this, tempAssetId, file, loader, callback, priority);
			executor.startTask(task);
		} else {
			queuedTask.merge(callback, priority);
		}
	}

	private <T> AssetLoader<T, AssetProperties> getLoader(String fileName, Class<T> assetType) {
		Factory<AssetLoader<T, AssetProperties>> factory = AssetDescriptors.getLoaderFactory(fileName, assetType);
		if (factory == null) {
			throw new IllegalStateException("No loader registered for asset type: " + assetType.getName());
		}

		@SuppressWarnings("unchecked")
		AssetLoader<T, AssetProperties> loader = (AssetLoader<T, AssetProperties>) loaders.get(factory);
		if (loader == null) {
			loader = factory.create();
			loaders.put(factory, loader);
		}

		return loader;
	}

	void finishTask(AssetLoadingTask<?> task) {
		boolean revert = task.exception != null;
		if (!revert) {
			AssetId assetId = task.assetId;
			boolean sticky = task.isSticky();
			int references = task.getReferences();
			int reservations = task.getReservations();
			ObjectIntMap<AssetId> dependencyCount = task.getDependencyCount();
			registry.add(assetId, task.asset, sticky, references, reservations, dependencyCount);
		}

		Entries<AssetId, Dependency<?>> entries = task.getDependencies();
		for (Entry<AssetId, Dependency<?>> entry : entries) {
			Dependency<?> dependency = entry.value;
			if (dependency instanceof AssetSlot) {
				registry.unreserve(dependency.getAssetId());
			} else {
				AssetLoadingTask<?> dependencyTask = (AssetLoadingTask<?>) dependency;
				if (dependencyTask.phase == finished && dependencyTask.asset != null) {
					registry.unreserve(dependency.getAssetId());
				}
			}
		}

		if (task.isRoot()) {
			freeTask(task);
		}

		System.out.println(getDiagnostics());
		System.out.println("--------------------------------------------\n\n");
	}

	private void freeTask(AssetLoadingTask<?> task) {
		for (Entry<AssetId, Dependency<?>> entry : task.getDependencies()) {
			Dependency<?> dependency = entry.value;
			if (dependency instanceof AssetLoadingTask) {
				// TODO check if unfinished and has concurrent callbacks -> make root task
				freeTask((AssetLoadingTask<?>) dependency);
			}
		}
		taskPool.free(task);
	}

	<T> Dependency<T> getDependency(AssetLoadingTask<?> parent, String fileName, FileType fileType,
			Class<T> assetType) {
		synchronized (mutex) {
			tempAssetId.set(fileName, fileType, assetType);
			Dependency<T> dependency = registry.reserve(tempAssetId);
			return dependency == null ? subTask(parent, assetType) : dependency;
		}
	}

	private <T> AssetLoadingTask<T> subTask(AssetLoadingTask<?> parent, Class<T> assetType) {
		AssetLoadingTask<T> queuedTask = executor.findTask(tempAssetId);
		if (queuedTask == null) {
			AssetLoadingTask<T> task = taskPool.obtainTask();
			String fileName = tempAssetId.fileName;
			FileHandle file = resolvers.resolveFile(tempAssetId);
			AssetLoader<T, AssetProperties> loader = getLoader(fileName, assetType);
			task.init(parent, tempAssetId, file, loader);
			executor.startTask(task);
			return task;
		} else {
			queuedTask.merge(parent, parent.priority);
			return queuedTask;
		}
	}

	boolean update() {
		synchronized (mutex) {
			return executor.update();
		}
	}

	boolean update(int millis) {
		long endTime = TimeUtils.millis() + millis;
		while (true) {
			boolean done = update();
			if (done || TimeUtils.millis() > endTime) {
				return done;
			}
			ThreadUtils.yield();
		}
	}

	void finishLoading() {
		while (!update()) {
			ThreadUtils.yield();
		}
	}

	void finishLoading(String fileName, FileType fileType, Class<?> assetType) {
		while (!isLoaded(fileName, fileType, assetType)) {
			update();
			ThreadUtils.yield();
		}
	}

	boolean fileExists(String fileName, FileType fileType) {
		return getFileHandle(fileName, fileType).exists();
	}

	FileHandle getFileHandle(String path, FileType type) {
		return files.getFileHandle(path, type);
	}

	void registerResolver(FileHandleResolver resolver) {
		resolvers.registerResolver(resolver);
	}

	boolean unregisterResolver(FileHandleResolver resolver) {
		return resolvers.unregisterResolver(resolver);
	}

	@Override
	public void onCleanup() {
		synchronized (mutex) {
			executor.update();
		}
	}

	@Override
	public void dispose() {
		synchronized (mutex) {
			executor.dispose();
			registry.dispose();
		}
	}

	String getDiagnostics() {
		return "registry:\n" + registry.getDiagnostics() + "\n" + "executor:\n" + executor.getDiagnostics();
	}
}
