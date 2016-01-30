package com.gurella.engine.asset.manager;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.utils.SynchronizedPools;

class AssetLoadingTask2<T> implements AsyncTask<Void>, Comparable<AssetLoadingTask2<?>>, Poolable {
	private AssetManager2 manager;
	private AssetLoader<T, AssetLoaderParameters<T>> loader;
	private AsyncCallback<T> callback;

	String fileName;
	Class<T> type;
	AssetLoaderParameters<T> params;

	private FileHandle file;

	private int priority;
	private long startTime;

	AssetLoadingTask2<?> parent;
	private final Array<AssetLoadingTask2<?>> dependencies = new Array<AssetLoadingTask2<?>>();

	volatile boolean cancel = false;
	private volatile LoadingState loadingState = LoadingState.ready;
	private volatile float progress = 0;

	private volatile T asset = null;

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
		task.startTime = TimeUtils.nanoTime();
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
		task.startTime = TimeUtils.nanoTime();
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
			//TODO notify manager
			onException(exception);
		}

		return null;
	}

	private void start() {
		FileHandle file = getFile();
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Array<AssetDescriptor<?>> descriptors = (Array) loader.getDependencies(fileName, file, params);
		if (descriptors == null || descriptors.size == 0) {
			loadAsync();
		} else {
			removeDuplicates(descriptors);
			initDependencies(descriptors);
			loadingState = LoadingState.waitingForDependencies;
			manager.injectDependencies(dependencies);
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
			SynchronousAssetLoader<T, AssetLoaderParameters<T>> syncLoader = (SynchronousAssetLoader<T, AssetLoaderParameters<T>>) loader;
			asset = syncLoader.load(manager, fileName, file, params);
			loadingState = LoadingState.finished;
			onSuccess(asset);
		} else {
			AsynchronousAssetLoader<T, AssetLoaderParameters<T>> asyncLoader = (AsynchronousAssetLoader<T, AssetLoaderParameters<T>>) loader;
			asyncLoader.loadAsync(manager, fileName, file, params);
			loadingState = LoadingState.readyForSyncLoading;
		}
	}

	void loadSync() {
		AsynchronousAssetLoader<T, AssetLoaderParameters<T>> asyncLoader = (AsynchronousAssetLoader<T, AssetLoaderParameters<T>>) loader;
		asset = asyncLoader.loadSync(manager, fileName, getFile(), params);
		loadingState = LoadingState.finished;
		onSuccess(asset);
	}

	private FileHandle getFile() {
		if (file == null) {
			file = loader.resolve(fileName);
		}
		return file;
	}

	private static void removeDuplicates(Array<AssetDescriptor<?>> descriptors) {
		boolean ordered = descriptors.ordered;
		descriptors.ordered = true;

		for (int i = 0; i < descriptors.size; ++i) {
			AssetDescriptor<?> dependency = descriptors.get(i);
			final String fileName = dependency.fileName;
			final Class<?> type = dependency.type;

			for (int j = descriptors.size - 1; j > i; --j) {
				AssetDescriptor<?> otherDependency = descriptors.get(j);
				if (type == otherDependency.type && fileName.equals(otherDependency.fileName)) {
					descriptors.removeIndex(j);
				}
			}
		}

		descriptors.ordered = ordered;
	}

	private void updateProgress() {
		switch (loadingState) {
		case ready: {
			onProgress(0);
			break;
		}
		case waitingForDependencies: {
			onProgress(0f);
			break;
		}
		case readyForAsyncLoading: {
			float progres = getDependenciesProgress();
			onProgress(loader instanceof SynchronousAssetLoader ? (0.9f * progres) : (0.8f * progres));
			break;
		}
		case readyForSyncLoading: {
			float progres = getDependenciesProgress();
			onProgress(0.9f * progres);
			break;
		}
		case finished: {
			onProgress(1);
			break;
		}
		default: {
			throw new IllegalStateException();
		}
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

		return progres / size;
	}

	private void onSuccess(T value) {
		progress = 1;
		if (callback == null) {
			parent.updateProgress();
		} else {
			callback.onProgress(progress);
			callback.onSuccess(value);
		}
	}

	private void onProgress(float progress) {
		this.progress = progress;
		if (callback == null) {
			parent.updateProgress();
		} else {
			callback.onProgress(progress);
		}
	}

	private void onException(Throwable exception) {
		this.progress = 1;
		if (callback == null) {
			parent.onException(exception);
		} else {
			callback.onException(exception);
		}
	}

	@Override
	public int compareTo(AssetLoadingTask2<?> other) {
		int result = Integer.compare(other.priority, priority);
		return result == 0 ? Long.compare(startTime, other.startTime) : result;
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
		startTime = 0;
		cancel = false;
		SynchronizedPools.freeAll(dependencies);
		dependencies.clear();
		loadingState = LoadingState.ready;
		progress = 0;
	}

	void free() {
		SynchronizedPools.free(this);
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
		ready, waitingForDependencies, readyForSyncLoading, readyForAsyncLoading, finished;
	}
}
