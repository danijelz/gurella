package com.gurella.engine.asset2.loader;

import static com.gurella.engine.asset2.loader.AssetLoadingState.waitingDependencies;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.disposable.DisposablesService;

public class AssetsLoader implements Disposable {
	private final Object mutex = new Object();
	private final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));
	private final TaskPool taskPool = new TaskPool();

	private final ObjectMap<AssetId, AssetLoadingTask<?, ?>> allTasks = new ObjectMap<AssetId, AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> asyncQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> waitingQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> syncQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> failedTasks = new Array<AssetLoadingTask<?, ?>>();
	private final Sort sort = new Sort();

	private final AssetId tempAssetId = new AssetId();

	public <T> void load(AsyncCallback<T> callback, FileHandle file, Class<T> assetType, int priority) {
		synchronized (mutex) {
			tempAssetId.set(file, assetType);
			@SuppressWarnings("unchecked")
			AssetLoadingTask<?, T> queuedTask = (AssetLoadingTask<?, T>) allTasks.get(tempAssetId);
			if (queuedTask == null) {
				@SuppressWarnings("unchecked")
				AssetLoadingTask<T, ?> task = (AssetLoadingTask<T, ?>) taskPool.obtain();
				task.init(this, loader, file, callback, priority);
				asyncQueue.add(task);
				sort.sort(asyncQueue);
			} else {
				queuedTask.merge(callback, priority);
			}
		}
	}

	public boolean update() {
		synchronized (mutex) {
			processFailedTasks();
			processSyncQueue();
			processNextAsyncTask();
			return asyncQueue.size == 0 && syncQueue.size == 0 && waitingQueue.size == 0;
		}
	}

	private void processFailedTasks() {
		for (int i = 0, n = failedTasks.size; i < n; i++) {
			AssetLoadingTask<?, ?> task = failedTasks.get(i);
			notifyException(task);
		}
		syncQueue.clear();
	}

	private void processSyncQueue() {
		for (int i = 0, n = syncQueue.size; i < n; i++) {
			AssetLoadingTask<?, ?> task = syncQueue.get(i);
			task.consumeAsyncData();
			finishTask(task);
		}
		syncQueue.clear();
	}

	private void notifyException(AssetLoadingTask<?, ?> task) {
		unloadLoadedDependencies(task);
		Throwable ex = task.exception;
		propagateException(task, ex);
		task.free();
	}

	private <T> void finishTask(AssetLoadingTask<?, T> task) {
		//TODO add to registry
		notifyTaskFinished(task);
		taskPool.free(task);
	}

	private void processNextAsyncTask() {
		if (asyncQueue.size > 0) {
			AssetLoadingTask<?, ?> nextTask = asyncQueue.pop();
			waitingQueue.add(nextTask);
			executor.submit(nextTask);
		}
	}

	void waitingDependencies(AssetLoadingTask<?, ?> task) {
		synchronized (mutex) {
			Array<AssetLoadingTask<?, ?>> dependencies = task.dependencies;
			for (int i = 0; i < dependencies.size; i++) {
				AssetLoadingTask<?, ?> dependency = dependencies.get(i);
				AssetInfo info = assetsByFileName.get(dependency.fileName);
				if (info == null) {
					addToQueue(dependency);
				} else {
					handleAssetLoaded(dependency, info);
				}
			}
		}
	}

	public void loadSync(AssetLoadingTask<?, ?> assetLoadingTask) {
		// TODO Auto-generated method stub

	}

	public void loadAsync(AssetLoadingTask<?, ?> assetLoadingTask) {
		// TODO Auto-generated method stub

	}

	public void finishLoading() {
		while (!update()) {
			ThreadUtils.yield();
		}
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

	@Override
	public void dispose() {
		finishLoading();
		DisposablesService.dispose(executor);
	}

	private static class TaskPool extends Pool<AssetLoadingTask<?, ?>> {
		@Override
		protected AssetLoadingTask<Object, Object> newObject() {
			return new AssetLoadingTask<Object, Object>();
		}
	}
}
