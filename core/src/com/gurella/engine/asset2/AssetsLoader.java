package com.gurella.engine.asset2;

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
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationCleanupListener;
import com.gurella.engine.subscriptions.asset.AssetActivityListener;
import com.gurella.engine.utils.priority.Priority;

@Priority(value = Integer.MIN_VALUE, type = ApplicationCleanupListener.class)
class AssetsLoader implements ApplicationCleanupListener, Disposable, AsyncTask<Void> {
	private final Object mutex = new Object();
	private final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));
	private final TaskPool taskPool = new TaskPool();
	private boolean executing;

	private final ObjectMap<AssetId, AssetLoadingTask<?, ?>> allTasks = new ObjectMap<AssetId, AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> asyncQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> waitingQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> finishedQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Sort sort = new Sort();

	private final AssetId tempAssetId = new AssetId();

	<T> void load(AsyncCallback<T> callback, FileHandle file, Class<T> assetType, int priority) {
		synchronized (mutex) {
			tempAssetId.set(file, assetType);
			@SuppressWarnings("unchecked")
			AssetLoadingTask<?, T> queuedTask = (AssetLoadingTask<?, T>) allTasks.get(tempAssetId);
			if (queuedTask == null) {
				loadAsync(callback, file, assetType, priority);
			} else {
				queuedTask.merge(callback, priority);
			}
		}
	}


	private <T, A> void loadAsync(AsyncCallback<T> callback, FileHandle file, Class<T> assetType, int priority) {
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
			unloadLoadedDependencies(task);
		}

		task.notifyFinished();
		taskPool.free(task);
	}
	
	private <T> void notifyTaskFinished(AssetLoadingTask<?, T> task) {
		String fileName = task.fileName;
		Class<T> type = task.type;

		notifyLoadFinished(fileName, type, task.params, task.callback, asset);
		Array<AssetLoadingTask<T>> concurentTasks = task.concurentTasks;
		for (int i = 0; i < concurentTasks.size; i++) {
			AssetLoadingTask<T> competingTask = concurentTasks.get(i);
			notifyLoadFinished(fileName, type, competingTask.params, competingTask.callback, asset);
		}
	}

	@Override
	public Void call() throws Exception {
		AssetLoadingTask<?, ?> nextTask;

		while (true) {
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
					waitingQueue.add(nextTask);
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
					// TODO error
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
	public void cleanup() {
		update();
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
