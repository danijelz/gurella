package com.gurella.engine.asset.manager;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.utils.SynchronizedPools;
import com.gurella.engine.utils.ValueUtils;

class AssetLoadingTask2<T> implements AsyncTask<Void>, Comparable<AssetLoadingTask2<?>>, Poolable {
	private static int counter = Integer.MIN_VALUE;

	int loadRequestId;
	int priority;

	AssetManager2 manager;
	AssetLoader<T, AssetLoaderParameters<T>> loader;
	AsyncCallback<T> callback;

	String fileName;
	FileHandle file;
	Class<T> type;
	AssetLoaderParameters<T> params;

	AssetLoadingTask2<?> parent;
	final Array<AssetLoadingTask2<?>> dependencies = new Array<AssetLoadingTask2<?>>();
	final Array<AssetLoadingTask2<T>> competingTasks = new Array<AssetLoadingTask2<T>>();

	volatile LoadingState loadingState = LoadingState.ready;
	volatile float progress = 0;
	volatile int loadRequests = 1;
	volatile int cancleRequests = 0;
	volatile T asset;
	volatile Throwable exception;

	static <T> AssetLoadingTask2<T> obtain(AssetManager2 manager, AsyncCallback<T> callback, String fileName,
			Class<T> type, AssetLoaderParameters<T> params, int priority) {
		@SuppressWarnings("unchecked")
		AssetLoadingTask2<T> task = SynchronizedPools.obtain(AssetLoadingTask2.class);
		task.manager = manager;
		task.loader = manager.findLoader(type, fileName);
		task.fileName = fileName.replaceAll("\\\\", "/");
		task.type = type;
		task.params = params;
		task.priority = priority;
		task.callback = callback;
		task.loadRequestId = counter++;
		return task;
	}

	static <T> AssetLoadingTask2<T> obtain(AssetLoadingTask2<?> parent, String fileName, FileHandle file, Class<T> type,
			AssetLoaderParameters<T> params) {
		@SuppressWarnings("unchecked")
		AssetLoadingTask2<T> task = SynchronizedPools.obtain(AssetLoadingTask2.class);
		task.manager = parent.manager;
		task.loader = parent.manager.findLoader(type, fileName);
		task.fileName = fileName;
		task.file = file;
		task.type = type;
		task.params = params;
		task.priority = parent.priority;
		task.parent = parent;
		task.loadRequestId = parent.loadRequestId;
		return task;
	}

	@Override
	@SuppressWarnings("fallthrough")
	public Void call() throws Exception {
		try {
			switch (loadingState) {
			case ready:
				start();
				break;
			case readyForAsyncLoading:
				loadAsync();
			default:
				throw new IllegalStateException();
			}
		} catch (Exception exception) {
			this.exception = exception;
			manager.exception(this);
		}

		return null;
	}

	private void start() {
		if (file == null) {
			file = loader.resolve(fileName);
		}

		Array<AssetDescriptor<?>> descriptors = ValueUtils.cast(loader.getDependencies(fileName, file, params));
		if (descriptors == null || descriptors.size == 0) {
			loadAsync();
		} else {
			removeDuplicates(descriptors);
			initDependencies(descriptors);
			manager.waitingForDependencies(this);
		}
	}

	private void initDependencies(Array<AssetDescriptor<?>> descriptors) {
		for (int i = 0; i < descriptors.size; i++) {
			@SuppressWarnings("unchecked")
			AssetDescriptor<Object> descriptor = (AssetDescriptor<Object>) descriptors.get(i);
			@SuppressWarnings("unchecked")
			AssetLoaderParameters<Object> castedParams = descriptor.params;
			dependencies.add(obtain(this, descriptor.fileName, descriptor.file, descriptor.type, castedParams));
		}
	}

	private void loadAsync() {
		if (loader instanceof SynchronousAssetLoader) {
			SynchronousAssetLoader<T, AssetLoaderParameters<T>> syncLoader = ValueUtils.cast(loader);
			asset = syncLoader.load(manager, fileName, file, params);
			manager.finished(this);
		} else {
			AsynchronousAssetLoader<T, AssetLoaderParameters<T>> asyncLoader = ValueUtils.cast(loader);
			asyncLoader.loadAsync(manager, fileName, file, params);
			manager.readyForSyncLoading(this);
		}
	}

	void loadSync() {
		AsynchronousAssetLoader<T, AssetLoaderParameters<T>> asyncLoader = ValueUtils.cast(loader);
		asset = asyncLoader.loadSync(manager, fileName, file, params);
		manager.finished(this);
	}

	private void removeDuplicates(Array<AssetDescriptor<?>> descriptors) {
		boolean ordered = descriptors.ordered;
		descriptors.ordered = true;

		for (int i = 0; i < descriptors.size; ++i) {
			AssetDescriptor<?> dependency = descriptors.get(i);
			final String fileName = dependency.fileName;
			final Class<?> type = dependency.type;

			for (int j = descriptors.size - 1; j > i; --j) {
				AssetDescriptor<?> otherDependency = descriptors.get(j);
				if (fileName.equals(otherDependency.fileName)) {
					if (type == otherDependency.type) {
						descriptors.removeIndex(j);
					} else {
						exception = new GdxRuntimeException("Dependencies conflict.");
						manager.exception(this);
					}
				}
			}
		}

		descriptors.ordered = ordered;
	}

	void notifyProgress(float progress) {
		this.progress = progress;
		if (parent != null) {
			parent.updateProgress();
		} else if (callback != null) {
			callback.onProgress(progress);
		}
		
		for (int i = 0; i < competingTasks.size; i++) {
			competingTasks.get(i).notifyProgress(progress);
		}
	}

	private void updateProgress() {
		switch (loadingState) {
		case ready:
			notifyProgress(0);
			break;
		case waitingForDependencies:
			float progress = getDependenciesProgress();
			notifyProgress(loader instanceof SynchronousAssetLoader ? (0.9f * progress) : (0.8f * progress));
			if (progress == 1) {
				manager.readyForAsyncLoading(this);
			}
			break;
		case readyForAsyncLoading:
			notifyProgress(loader instanceof SynchronousAssetLoader ? (0.9f) : (0.8f));
			break;
		case readyForSyncLoading:
			notifyProgress(0.9f);
			break;
		case finished:
		case error:
			notifyProgress(1);
			break;
		default:
			throw new IllegalStateException();
		}
	}

	private float getDependenciesProgress() {
		int size = dependencies.size;
		if (size == 0) {
			return 1;
		}

		float progres = 0;
		for (int i = 0; i < size; i++) {
			AssetLoadingTask2<?> dependency = dependencies.get(i);
			progres += dependency.progress;
		}

		return Math.min(1, progres / size);
	}

	private void handleException(Throwable exception) {
		loadingState = LoadingState.error;
		this.progress = 1;

		if (parent != null) {
			// TODO notifyManager
			unloadDependencies();
			parent.handleException(exception);
		} else if (callback != null) {
			callback.onException(exception);
		} else {
			throw new GdxRuntimeException(exception);
		}
	}

	private void unloadDependencies() {
		for (int i = 0; i < dependencies.size; i++) {
			AssetLoadingTask2<?> dependency = dependencies.get(i);
			if (dependency.progress == 1) {
				try {
					manager.unloadAsset(dependency.fileName);
				} catch (Exception e) {
				}
			}
		}
	}

	void merge(AssetLoadingTask2<T> competingTask) {
		competingTasks.add(competingTask);
		loadRequests++;
		int newPriority = competingTask.priority;
		if (priority < newPriority) {
			reniceHierarchy(competingTask.loadRequestId, newPriority);
		}
	}

	private void reniceHierarchy(int newLoadRequestId, int newPriority) {
		loadRequestId = newLoadRequestId;
		priority = newPriority;
		for (int i = 0; i < dependencies.size; i++) {
			AssetLoadingTask2<?> dependency = dependencies.get(i);
			dependency.reniceHierarchy(newLoadRequestId, newPriority);
		}
	}

	@Override
	public int compareTo(AssetLoadingTask2<?> other) {
		int result = Integer.compare(other.priority, priority);
		return result == 0 ? Long.compare(loadRequestId, other.loadRequestId) : result;
	}

	@Override
	public void reset() {
		manager = null;
		loader = null;
		callback = null;
		priority = 0;
		fileName = null;
		type = null;
		params = null;
		file = null;
		progress = 0;
		loadingState = LoadingState.ready;
		loadRequestId = Integer.MAX_VALUE;
		SynchronizedPools.freeAll(dependencies);
		dependencies.clear();
		for (int i = 0; i < competingTasks.size; i++) {
			competingTasks.get(i).free();
		}
		dependencies.clear();
		asset = null;
		exception = null;
		loadRequests = 1;
		cancleRequests = 0;
	}

	void free() {
		if (parent == null) {
			SynchronizedPools.free(this);
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(fileName);
		buffer.append(" ");
		buffer.append(type.getName());
		return buffer.toString();
	}

	enum LoadingState {
		ready, waitingForDependencies, readyForSyncLoading, readyForAsyncLoading, finished, error;
	}
}