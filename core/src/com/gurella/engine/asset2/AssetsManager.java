package com.gurella.engine.asset2;

import static com.gurella.engine.asset2.AssetLoadingState.finished;

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
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.asset2.persister.AssetIdResolver;
import com.gurella.engine.asset2.persister.AssetPersister;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.async.SimpleAsyncCallback;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.subscriptions.application.ApplicationCleanupListener;
import com.gurella.engine.utils.priority.Priority;

@Priority(value = Integer.MIN_VALUE, type = ApplicationCleanupListener.class)
class AssetsManager implements ApplicationCleanupListener, AssetIdResolver, AsyncTask<Void>, Disposable {
	private final Object mutex = new Object();

	private final Files files = Gdx.files;
	private final AssetRegistry registry = new AssetRegistry();
	private final AssetDescriptors descriptors = new AssetDescriptors();
	private final AssetId tempAssetId = new AssetId();

	private final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));
	private final TaskPool taskPool = new TaskPool();
	private boolean executing;

	private final ObjectMap<AssetId, AssetLoadingTask<?, ?>> allTasks = new ObjectMap<AssetId, AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> asyncQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> waitingQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> finishedQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Sort sort = new Sort();

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

	<T> void save(T asset, String fileName, FileType fileType) {
		synchronized (mutex) {
			FileHandle file = files.getFileHandle(fileName, fileType);
			Class<T> assetType = descriptors.getAssetType(asset);
			AssetPersister<T> persister = descriptors.getPersister(assetType, fileName);
			persister.persist(this, file, asset);

			registry.getAssetId(asset, tempAssetId);
			if (!tempAssetId.isEmpty() && !tempAssetId.equalsFile(fileName, fileType)) {
				throw new IllegalStateException("Asset allready persisted on another location.");
			}

			tempAssetId.set(file, assetType);
			registry.add(tempAssetId, asset);
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

	private <T> void load(AsyncCallback<T> callback, String fileName, FileType fileType, Class<T> assetType,
			int priority) {
		tempAssetId.set(fileName, fileType, assetType);
		@SuppressWarnings("unchecked")
		AssetLoadingTask<?, T> queuedTask = (AssetLoadingTask<?, T>) allTasks.get(tempAssetId);

		if (queuedTask == null) {
			AssetLoadingTask<?, T> task = taskPool.obtainTask();
			task.init(this, resolveFile(fileName, fileType), assetType, callback, priority);
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
		asyncQueue.add(task);
		sort.sort(asyncQueue);
		if (!executing) {
			executing = true;
			executor.submit(this);
		}
	}

	boolean update() {
		synchronized (mutex) {
			for (int i = 0, n = finishedQueue.size; i < n; i++) {
				AssetLoadingTask<?, ?> task = finishedQueue.get(i);
				allTasks.remove(task.assetId);
				task.update();
				finishTask(task);
			}
			finishedQueue.clear();
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
			registry.unreserve(entry.value.getAssetId());
		}

		if (task.isRoot()) {
			freeTask(task);
		}
	}

	private void freeTask(AssetLoadingTask<?, ?> task) {
		for (Entry<AssetId, Dependency<?>> entry : task.getDependencies()) {
			Dependency<?> dependency = entry.value;
			if (dependency instanceof AssetLoadingTask) {
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

	private <T> AssetLoadingTask<?, T> subTask(AssetLoadingTask<?, ?> parent, Class<T> assetType) {
		@SuppressWarnings("unchecked")
		AssetLoadingTask<?, T> queuedTask = (AssetLoadingTask<?, T>) allTasks.get(tempAssetId);
		if (queuedTask == null) {
			AssetLoadingTask<?, T> task = taskPool.obtainTask();
			task.init(parent, resolveFile(tempAssetId.fileName, tempAssetId.fileType), assetType);
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
			switch (task.state) {
			case waitingDependencies:
				waitingQueue.add(task);
				return;
			case asyncLoading:
				asyncQueue.add(task);
				return;
			case syncLoading:
				finishedQueue.add(task);
				return;
			case finished:
				finishedQueue.add(task);
				return;
			default:
				task.state = finished;
				task.exception = new IllegalStateException("Invalid loading state.");
				finishedQueue.add(task);
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
		return files.getFileHandle(fileName, fileType).exists();
	}

	public <T> AssetDescriptor<T> getAssetDescriptor(final Class<? extends T> assetType) {
		return descriptors.getAssetDescriptor(assetType);
	}

	public <T> AssetDescriptor<T> getAssetDescriptor(final String fileName) {
		return descriptors.getAssetDescriptor(fileName);
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
		<T> AssetLoadingTask<?, T> obtainTask() {
			return (AssetLoadingTask<?, T>) super.obtain();
		}
	}
}
