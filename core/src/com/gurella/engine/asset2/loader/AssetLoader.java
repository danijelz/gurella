package com.gurella.engine.asset2.loader;

import static com.gurella.engine.asset2.loader.AssetLoadingState.waitingDependencies;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.disposable.DisposablesService;

public class AssetLoader implements Disposable {
	private final Object mutex = new Object();
	private final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));

	private final ObjectMap<AssetId, AssetLoadingTask<?, ?>> allTasks = new ObjectMap<AssetId, AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> asyncQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> waitingQueue = new Array<AssetLoadingTask<?, ?>>();
	private final Array<AssetLoadingTask<?, ?>> syncQueue = new Array<AssetLoadingTask<?, ?>>();
	private AssetLoadingTask<?, ?> currentTask;

	private final AssetId tempAssetId = new AssetId();

	public <T> void load(AsyncCallback<T> callback, String fileName, FileType fileType, Class<T> assetType,
			int priority) {
		synchronized (mutex) {
			tempAssetId.set(fileName, fileType, assetType);
			@SuppressWarnings("unchecked")
			AssetLoadingTask<?, T> queuedTask = (AssetLoadingTask<?, T>) allTasks.get(tempAssetId);
			if (queuedTask == null) {
				asyncQueue.add(obtain(this, callback, fileName, fileType, assetType, priority));
			} else {
				queuedTask.merge(obtain(this, callback, fileName, fileType, assetType, priority));
			}
			asyncQueue.sort();
		}
	}

	public boolean update() {
		synchronized (mutex) {
			processCurrentTaskException();
			processSyncQueue();
			processNextAsyncTask();
			return asyncQueue.size == 0 && syncQueue.size == 0 && waitingQueue.size == 0;
		}
	}

	private void processCurrentTaskException() {
		if (currentTask != null && currentTask.exception != null) {
			if (!waitingQueue.removeValue(currentTask, true)) {
				throw new IllegalStateException();
			}

			currentTask = null;
			handleTaskException(currentTask);
		}
	}

	private void processSyncQueue() {
		while (syncQueue.size > 0) {
			AssetLoadingTask<?, ?> task = syncQueue.removeIndex(0);
			task.consumeAsyncData();
			finishTask(task);
		}
	}

	private void handleTaskException(AssetLoadingTask<?, ?> task) {
		unloadLoadedDependencies(task);
		Throwable ex = task.exception;
		propagateException(task, ex);
		task.free();
	}

	private <T> void finishTask(AssetLoadingTask<?, T> task) {
		String fileName = task.fileName;
		AssetInfo info = task.info;
		T asset = info.getAsset();
		fileNamesByAsset.put(asset, fileName);
		assetsByFileName.put(fileName, info);

		notifyTaskFinished(task, asset);
		task.free();
	}

	private void processNextAsyncTask() {
		if (currentTask == null && asyncQueue.size > 0) {
			AssetLoadingTask<?, ?> nextTask = asyncQueue.removeIndex(0);
			waitingQueue.add(nextTask);
			currentTask = nextTask;
			executor.submit(nextTask);
		}
	}

	void waitingDependencies(AssetLoadingTask<?, ?> task) {
		synchronized (mutex) {
			currentTask = null;
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
}
