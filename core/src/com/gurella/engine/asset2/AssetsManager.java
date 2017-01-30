package com.gurella.engine.asset2;

import static com.gurella.engine.asset2.AssetLoadingState.finished;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.asset2.bundle.Bundle;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.persister.AssetsPersister;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.async.SimpleAsyncCallback;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.subscriptions.application.ApplicationCleanupListener;
import com.gurella.engine.utils.priority.Priority;

@Priority(value = Integer.MIN_VALUE, type = ApplicationCleanupListener.class)
public class AssetsManager implements ApplicationCleanupListener, AsyncTask<Void>, Disposable {
	private final Object mutex = new Object();

	private final Files files = Gdx.files;
	private final AssetRegistry registry = new AssetRegistry();
	private final AssetsPersister persister = new AssetsPersister(this);
	private final AssetId tempAssetId = new AssetId();

	private final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));
	private final TaskPool taskPool = new TaskPool();
	private boolean executing;

	private final ObjectMap<AssetId, AssetLoadingTask<?, ?>> allTasks = new ObjectMap<AssetId, AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> asyncQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> waitingQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> finishedQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Sort sort = new Sort();

	//TODO remove when implemented in AssetSevice
	public boolean isLoaded(String fileName) {
		synchronized (mutex) {
			return registry.isLoaded(fileName, FileType.Internal, Assets.getAssetClass(fileName));
		}
	}

	public boolean isLoaded(String fileName, FileType fileType, Class<?> assetType) {
		synchronized (mutex) {
			return registry.isLoaded(fileName, fileType, assetType);
		}
	}

	public <T> boolean unload(T asset) {
		synchronized (mutex) {
			return registry.decRefCount(asset);
		}
	}

	public <T> Array<T> getAll(Class<T> type, Array<T> out) {
		synchronized (mutex) {
			return registry.getAll(type, out);
		}
	}

	public <T> String getFileName(T asset) {
		synchronized (mutex) {
			return registry.getFileName(asset);
		}
	}

	public <T> FileType getFileType(T asset) {
		synchronized (mutex) {
			return registry.getFileType(asset);
		}
	}

	public <T> AssetId getId(T asset, AssetId out) {
		synchronized (mutex) {
			return registry.getAssetId(asset, out);
		}
	}

	public boolean isManaged(Object asset) {
		return registry.isManaged(asset);
	}

	public <T> void save(T asset, String fileName, FileType fileType, boolean sticky) {
		synchronized (mutex) {
			FileHandle file = resolveFile(fileName, fileType);
			persister.persist(file, asset);
			if (!registry.isManaged(asset)) {
				Class<Object> assetType = Assets.getAssetRootClass(asset);
				registry.add(fileName, fileType, assetType, asset, sticky);
			}
		}
	}

	public boolean delete(String fileName, FileType fileType) {
		synchronized (mutex) {
			registry.removeAll(fileName, fileType);
			FileHandle file = files.getFileHandle(fileName, fileType);
			if (file.exists()) {
				file.delete();
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean delete(Object asset) {
		synchronized (mutex) {
			registry.getAssetId(asset, tempAssetId);
			if (tempAssetId.isEmpty()) {
				return false;
			}

			String fileName = tempAssetId.fileName;
			FileType fileType = tempAssetId.fileType;
			registry.removeAll(fileName, fileType);

			FileHandle file = files.getFileHandle(fileName, fileType);
			if (file.exists()) {
				file.delete();
				return true;
			} else {
				return false;
			}
		}
	}

	// TODO dependencies and bundle contents should be handled diferently
	public void addDependency(Object asset, Object dependency) {
		synchronized (mutex) {
			registry.addDependency(asset, dependency);
		}
	}

	public void removeDependency(Object asset, Object dependency) {
		synchronized (mutex) {
			registry.removeDependency(asset, dependency);
		}
	}

	public void replaceDependency(Object asset, Object oldDependency, Object newDependency) {
		synchronized (mutex) {
			registry.replaceDependency(asset, oldDependency, newDependency);
		}
	}

	public void addToBundle(Bundle bundle, Object asset, String internalId) {
		synchronized (mutex) {
			registry.addToBundle(bundle, asset, internalId);
		}
	}

	public void removeFromBundle(Bundle bundle, Object asset) {
		synchronized (mutex) {
			registry.removeFromBundle(bundle, asset);
		}
	}

	public String getBundledId(Object asset) {
		synchronized (mutex) {
			return registry.getBundleId(asset);
		}
	}

	//////////////////////////////

	public <T> void loadAsync(AsyncCallback<T> callback, String fileName, FileType fileType, Class<T> assetType,
			int priority) {
		synchronized (mutex) {
			T asset = registry.getIfLoaded(fileName, fileType, assetType, null);
			if (asset == null) {
				FileHandle file = resolveFile(fileName, fileType);
				load(callback, file, assetType, priority);
			} else {
				callback.onProgress(1f);
				callback.onSuccess(asset);
			}
		}
	}

	public <T> T load(String fileName, FileType fileType, Class<T> assetType) {
		synchronized (mutex) {
			T asset = registry.getIfLoaded(fileName, fileType, assetType, null);
			if (asset != null) {
				return asset;
			}

			SimpleAsyncCallback<T> callback = SimpleAsyncCallback.obtain();
			FileHandle file = resolveFile(fileName, fileType);
			load(callback, file, assetType, Integer.MAX_VALUE);

			while (!callback.isDone()) {
				update();
				ThreadUtils.yield();
			}

			if (callback.isDoneWithException()) {
				Throwable exception = callback.getException();
				callback.free();
				throw new RuntimeException("Error loading asset " + fileName, exception);
			} else {
				asset = callback.getValue();
				callback.free();
				return asset;
			}
		}
	}

	private FileHandle resolveFile(String fileName, FileType fileType) {
		// TODO resolve by AssetConfig
		return files.getFileHandle(fileName, fileType);
	}

	<T> void load(AsyncCallback<T> callback, FileHandle file, Class<T> assetType, int priority) {
		synchronized (mutex) {
			tempAssetId.set(file, assetType);
			@SuppressWarnings("unchecked")
			AssetLoadingTask<?, T> queuedTask = (AssetLoadingTask<?, T>) allTasks.get(tempAssetId);
			if (queuedTask == null) {
				newTask(callback, file, assetType, priority);
			} else {
				queuedTask.merge(callback, priority);
			}
		}
	}

	private <T, A> void newTask(AsyncCallback<T> callback, FileHandle file, Class<T> assetType, int priority) {
		// TODO find loader
		AssetLoader<A, T, AssetProperties<T>> loader = null;
		@SuppressWarnings("unchecked")
		AssetLoadingTask<A, T> task = (AssetLoadingTask<A, T>) taskPool.obtain();
		task.init(loader, file, callback, priority);

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
				task.consumeAsyncData();
				finishTask(task);
			}
			finishedQueue.clear();
			return allTasks.size == 0;
		}
	}

	private <T> void finishTask(AssetLoadingTask<?, T> task) {
		allTasks.remove(task.assetId);

		if (task.exception != null) {
			unloadDependencies(task);
		}

		task.notifyFinished();
		taskPool.free(task);
	}

	private <T> void unloadDependencies(AssetLoadingTask<?, T> task) {
		// TODO Auto-generated method stub
	}

	@Override
	public Void call() throws Exception {
		while (true) {
			AssetLoadingTask<?, ?> nextTask;
			synchronized (mutex) {
				if (asyncQueue.size > 0) {
					nextTask = asyncQueue.pop();
				} else {
					executing = false;
					return null;
				}
			}

			nextTask.process();

			synchronized (mutex) {
				switch (nextTask.state) {
				case waitingDependencies:
					loadDependencies(nextTask);
					break;
				case asyncLoading:
					asyncQueue.add(nextTask);
					break;
				case syncLoading:
					finishedQueue.add(nextTask);
					break;
				case finished:
					finishedQueue.add(nextTask);
					break;
				default:
					nextTask.state = finished;
					nextTask.exception = new IllegalStateException("Invalid loading state.");
					break;
				}
			}
		}
	}

	private void loadDependencies(AssetLoadingTask<?, ?> task) {
		AssetDependencies dependencies = task.dependencies;
		for (int i = 0; i < dependencies.size; i++) {
			AssetLoadingTask<?, ?> dependency = dependencies.get(i);
			AssetInfo info = assetsByFileName.get(dependency.fileName);
			if (info == null) {
				addToQueue(dependency);
			} else {
				handleAssetLoaded(dependency, info);
			}
		}
		
		//TODO if all dependencies loaded
		waitingQueue.add(task);
	}

	public boolean update(int millis) {
		long endTime = TimeUtils.millis() + millis;
		while (true) {
			boolean done = update();
			if (done || TimeUtils.millis() > endTime) {
				return done;
			}
			ThreadUtils.yield();
		}
	}

	public void finishLoading() {
		while (!update()) {
			ThreadUtils.yield();
		}
	}

	@Override
	public void cleanup() {
		update();
	}

	@Override
	public void dispose() {
		synchronized (mutex) {
			finishLoading();
			DisposablesService.dispose(executor);
			registry.dispose();
		}
	}

	private static class TaskPool extends Pool<AssetLoadingTask<?, ?>> {
		@Override
		protected AssetLoadingTask<Object, Object> newObject() {
			return new AssetLoadingTask<Object, Object>();
		}
	}
}
