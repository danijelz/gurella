package com.gurella.engine.asset2.loader;

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
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.disposable.DisposablesService;

public class AssetsLoader implements Disposable, AsyncTask<Void> {
	private final Object mutex = new Object();
	private final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));
	private final TaskPool taskPool = new TaskPool();
	private boolean executing;

	private final ObjectMap<AssetId, AssetLoadingTask<?, ?>> allTasks = new ObjectMap<AssetId, AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> asyncQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> waitingQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> finishedQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> temp = new Array<AssetLoadingTask<?, ?>>();
	private final Sort sort = new Sort();

	private final AssetId tempAssetId = new AssetId();

	public <T, A> void load(AsyncCallback<T> callback, FileHandle file, Class<T> assetType, int priority) {
		synchronized (mutex) {
			tempAssetId.set(file, assetType);
			@SuppressWarnings("unchecked")
			AssetLoadingTask<?, T> queuedTask = (AssetLoadingTask<?, T>) allTasks.get(tempAssetId);
			if (queuedTask == null) {
				//TODO 
				AssetLoader<A, T, AssetProperties<T>> loader = null;
				@SuppressWarnings("unchecked")
				AssetLoadingTask<A, T> task = (AssetLoadingTask<A, T>) taskPool.obtain();
				task.init(loader, file, callback, priority);
				loadAsync(task);
			} else {
				queuedTask.merge(callback, priority);
			}
		}
	}

	public boolean update() {
		synchronized (mutex) {
			temp.addAll(finishedQueue);
			finishedQueue.clear();
		}

		for (int i = 0, n = temp.size; i < n; i++) {
			AssetLoadingTask<?, ?> task = temp.get(i);
			task.consumeAsyncData();
			finishTask(task);
		}
		temp.clear();

		return asyncQueue.size == 0 && waitingQueue.size == 0;
	}

	private <T> void finishTask(AssetLoadingTask<?, T> task) {
		if (task.exception == null) {
			//TODO add to registry
			notifyTaskFinished(task);
		} else {
			unloadLoadedDependencies(task);
			Throwable ex = task.exception;
			propagateException(task, ex);
		}

		taskPool.free(task);
	}

	private void loadAsync(AssetLoadingTask<?, ?> task) {
		synchronized (mutex) {
			asyncQueue.add(task);
			sort.sort(asyncQueue);
			if (!executing) {
				executing = true;
				executor.submit(this);
			}
		}
	}

	@Override
	public Void call() throws Exception {
		while (true) {
			AssetLoadingTask<?, ?> nextTask;
			synchronized (mutex) {
				if (asyncQueue.size > 0) {
					nextTask = asyncQueue.removeIndex(0);
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
					waitingQueue.add(nextTask);
					break;
				case asyncLoading:
					asyncQueue.add(nextTask);
					break;
				case finished:
					finishedQueue.add(nextTask);
					break;
				default:
					//TODO error
					break;
				}
			}
		}
	}

	void loadDependencies(AssetLoadingTask<?, ?> task) {
		synchronized (mutex) {
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

	public void finishLoading() {
		while (!update()) {
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
