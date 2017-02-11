package com.gurella.engine.asset;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.asset.bundle.Bundle;
import com.gurella.engine.asset.descriptor.AssetDescriptors;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.asset.persister.AssetPersister;
import com.gurella.engine.asset.persister.DependencyLocator;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.async.SimpleAsyncCallback;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.subscriptions.application.ApplicationCleanupListener;
import com.gurella.engine.utils.priority.Priority;

@Priority(value = Integer.MIN_VALUE, type = ApplicationCleanupListener.class)
class AssetsManager implements ApplicationCleanupListener, DependencyLocator, AsyncTask<Void>, Disposable {
	private final Object mutex = new Object();

	private final Files files = Gdx.files;
	private final AssetRegistry registry = new AssetRegistry();
	private final AssetId tempAssetId = new AssetId();

	private final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));
	private final TaskPool taskPool = new TaskPool();
	private boolean executing;

	private final ObjectMap<AssetId, AssetLoadingTask<?, ?>> allTasks = new ObjectMap<AssetId, AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> asyncQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> syncQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Sort sort = new Sort();

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
			registry.getAssetId(asset, tempAssetId);
			if (tempAssetId.isEmpty()) {
				throw new IllegalStateException("Asset is not yet persisted.");
			}

			String fileName = tempAssetId.fileName;
			@SuppressWarnings("unchecked")
			Class<T> assetType = (Class<T>) tempAssetId.assetType;
			AssetPersister<T> persister = AssetDescriptors.getPersister(fileName, assetType);
			if (persister == null) {
				throw new IllegalStateException("No persistor registered for asset type: " + assetType.getName());
			}

			FileHandle file = resolveFile(fileName, tempAssetId.fileType);
			persister.persist(this, file, asset);
		}
	}

	<T> void save(T asset, String fileName, FileType fileType) {
		synchronized (mutex) {
			Class<T> assetType = AssetDescriptors.getAssetType(asset);
			if (assetType == null) {
				throw new IllegalStateException(
						"No descrptor registered for asset type: " + asset.getClass().getName());
			}

			AssetPersister<T> persister = AssetDescriptors.getPersister(fileName, assetType);
			if (persister == null) {
				throw new IllegalStateException("No persistor registered for asset type: " + assetType.getName());
			}

			registry.getAssetId(asset, tempAssetId);
			if (tempAssetId.isEmpty()) {
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
			registry.getAssetId(asset, tempAssetId);
			if (tempAssetId.isEmpty()) {
				return DeleteResult.unexisting;
			}

			String fileName = tempAssetId.fileName;
			FileType fileType = tempAssetId.fileType;
			registry.removeAll(fileName, fileType);

			FileHandle file = resolveFile(fileName, fileType);
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
	<T> void loadAsync(AsyncCallback<T> callback, String fileName, FileType fileType, int priority) {
		loadAsync(callback, fileName, fileType, AssetDescriptors.<T> getAssetType(fileName), priority);
	}

	<T> void loadAsync(AsyncCallback<T> callback, String fileName, FileType fileType, Class<T> assetType,
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
		synchronized (mutex) {
			T asset = registry.getLoaded(tempAssetId.set(fileName, fileType, assetType), null);
			if (asset != null) {
				return asset;
			}

			SimpleAsyncCallback<T> callback = SimpleAsyncCallback.obtain();
			load(callback, fileName, fileType, assetType, Integer.MAX_VALUE);

			while (!callback.isDone()) {
				update();
				ThreadUtils.yield();
			}

			if (callback.isFailed()) {
				tempAssetId.set(fileName, fileType, assetType);
				throw new RuntimeException("Error loading asset " + tempAssetId, callback.getExceptionAndFree());
			} else {
				return callback.getValueAndFree();
			}
		}
	}

	private <A, T> void load(AsyncCallback<T> callback, String fileName, FileType fileType, Class<T> assetType,
			int priority) {
		tempAssetId.set(fileName, fileType, assetType);
		@SuppressWarnings("unchecked")
		AssetLoadingTask<?, T> queuedTask = (AssetLoadingTask<?, T>) allTasks.get(tempAssetId);

		if (queuedTask == null) {
			AssetLoadingTask<A, T> task = taskPool.obtainTask();
			FileHandle file = resolveFile(fileName, fileType);
			AssetLoader<A, T, AssetProperties> loader = AssetDescriptors.getLoader(fileName, assetType);
			task.init(this, file, assetType, loader, callback, priority);
			startTask(task);
		} else {
			queuedTask.merge(callback, priority);
		}
	}

	private FileHandle resolveFile(String fileName, FileType fileType) {
		// TODO resolve by FileHandleResolver
		return files.getFileHandle(fileName, fileType);
	}

	private <T> void startTask(AssetLoadingTask<?, T> task) {
		allTasks.put(task.assetId, task);
		addToAsyncQueue(task);
	}

	private <T> void addToAsyncQueue(AssetLoadingTask<?, T> task) {
		asyncQueue.add(task);
		sort.sort(asyncQueue);
		if (!executing) {
			executing = true;
			executor.submit(this);
		}
	}

	boolean update() {
		synchronized (mutex) {
			for (int i = 0; i < syncQueue.size; i++) {
				AssetLoadingTask<?, ?> task = syncQueue.get(i);
				allTasks.remove(task.assetId);
				task.update();
				finishTask(task);
			}
			syncQueue.clear();
			return allTasks.size == 0;
		}
	}

	private void finishTask(AssetLoadingTask<?, ?> task) {
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
			}
		}

		if (task.isRoot()) {
			freeTask(task);
		}
	}

	private void freeTask(AssetLoadingTask<?, ?> task) {
		for (Entry<AssetId, Dependency<?>> entry : task.getDependencies()) {
			Dependency<?> dependency = entry.value;
			if (dependency instanceof AssetLoadingTask) {
				//TODO check if unfinished and has concurrent callbacks -> make root task
				freeTask((AssetLoadingTask<?, ?>) dependency);
			}
		}
		taskPool.free(task);
	}

	<T> Dependency<T> getDependency(AssetLoadingTask<?, ?> parent, String fileName, FileType fileType,
			Class<T> assetType) {
		synchronized (mutex) {
			tempAssetId.set(fileName, fileType, assetType);
			Dependency<T> dependency = registry.reserve(tempAssetId);
			return dependency == null ? subTask(parent, assetType) : dependency;
		}
	}

	private <A, T> AssetLoadingTask<?, T> subTask(AssetLoadingTask<?, ?> parent, Class<T> assetType) {
		@SuppressWarnings("unchecked")
		AssetLoadingTask<?, T> queuedTask = (AssetLoadingTask<?, T>) allTasks.get(tempAssetId);
		if (queuedTask == null) {
			AssetLoadingTask<A, T> task = taskPool.obtainTask();
			String fileName = tempAssetId.fileName;
			FileHandle file = resolveFile(fileName, tempAssetId.fileType);
			AssetLoader<A, T, AssetProperties> loader = AssetDescriptors.getLoader(fileName, assetType);
			task.init(parent, file, assetType, loader);
			startTask(task);
			return task;
		} else {
			return queuedTask;
		}
	}

	@Override
	public Void call() throws Exception {
		while (true) {
			AssetLoadingTask<?, ?> task;
			synchronized (mutex) {
				if (asyncQueue.size > 0) {
					task = asyncQueue.pop();
				} else {
					executing = false;
					return null;
				}
			}

			task.update();
			taskStateChanged(task);
		}
	}

	void taskStateChanged(AssetLoadingTask<?, ?> task) {
		synchronized (mutex) {
			switch (task.phase) {
			case waitingDependencies:
				return;
			case async:
				addToAsyncQueue(task);
				return;
			case sync:
				syncQueue.add(task);
				return;
			case finished:
				syncQueue.add(task);
				return;
			default:
				task.onException(new IllegalStateException("Invalid loading state."));
				return;
			}
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

	@Override
	public void onCleanup() {
		update();
	}

	@Override
	public void dispose() {
		while (true) {
			finishLoading();
			synchronized (mutex) {
				if (allTasks.size == 0) {
					DisposablesService.dispose(executor);
					registry.dispose();
					return;
				}
			}
		}
	}

	private static class TaskPool extends Pool<AssetLoadingTask<?, ?>> {
		@Override
		protected AssetLoadingTask<Object, Object> newObject() {
			return new AssetLoadingTask<Object, Object>();
		}

		@SuppressWarnings("unchecked")
		<A, T> AssetLoadingTask<A, T> obtainTask() {
			return (AssetLoadingTask<A, T>) super.obtain();
		}
	}
}
