package com.gurella.engine.asset;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.async.AsyncExecutor;
import com.gurella.engine.disposable.DisposablesService;

class AssetLoadingExecutor implements AsyncTask<Void>, Disposable {
	private final AssetsManager manager;
	private final Object mutex;

	private final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));
	private boolean executing;

	private final ObjectMap<AssetId, AssetLoadingTask<?>> allTasks = new ObjectMap<AssetId, AssetLoadingTask<?>>();
	private final Array<AssetLoadingTask<?>> asyncQueue = new Array<AssetLoadingTask<?>>();
	private final Array<AssetLoadingTask<?>> syncQueue = new Array<AssetLoadingTask<?>>();
	private final Sort sort = new Sort();

	AssetLoadingExecutor(AssetsManager manager) {
		this.manager = manager;
		mutex = manager.mutex;
	}

	<T> AssetLoadingTask<T> findTask(AssetId assetId) {
		@SuppressWarnings("unchecked")
		AssetLoadingTask<T> task = (AssetLoadingTask<T>) allTasks.get(assetId);
		return task;
	}

	<T> void startTask(AssetLoadingTask<T> task) {
		allTasks.put(task.assetId, task);
		addToAsyncQueue(task);
	}

	private <T> void addToAsyncQueue(AssetLoadingTask<T> task) {
		asyncQueue.add(task);
		sort.sort(asyncQueue);
		if (!executing) {
			executing = true;
			executor.submit(this);
		}
	}

	boolean update() {
		for (int i = 0; i < syncQueue.size; i++) {
			AssetLoadingTask<?> task = syncQueue.get(i);
			allTasks.remove(task.assetId);
			task.update();
			manager.finishTask(task);
		}
		syncQueue.clear();
		return allTasks.size == 0;
	}

	@Override
	public Void call() throws Exception {
		while (true) {
			AssetLoadingTask<?> task;
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

	void taskStateChanged(AssetLoadingTask<?> task) {
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

	@Override
	public void dispose() {
		syncQueue.clear();
		asyncQueue.clear();
		allTasks.clear();
		DisposablesService.dispose(executor);
	}

	String getDiagnostics() {
		StringBuilder builder = new StringBuilder();
		for (ObjectMap.Entry<AssetId, AssetLoadingTask<?>> entry : allTasks.entries()) {
			AssetLoadingTask<?> task = entry.value;
			builder.append(task.toString());
			builder.append("\n");
		}

		return builder.toString();
	}
}
