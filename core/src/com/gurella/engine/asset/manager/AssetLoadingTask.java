package com.gurella.engine.asset.manager;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.utils.SynchronizedPools;

class AssetLoadingTask<T> implements AsyncTask<Void>, Comparable<AssetLoadingTask<?>>, AsyncCallback<T>, Poolable {
	AssetManager manager;
	AssetLoader<T, AssetLoaderParameters<T>> loader;
	AsyncCallback<T> callback;
	String fileName;
	FileHandle file;
	Class<T> type;
	AssetLoaderParameters<T> params;
	int priority;

	long startTime;

	final Array<DependencyCallback<?>> dependencies = new Array<DependencyCallback<?>>();

	volatile boolean asyncDone = false;
	volatile boolean dependenciesLoaded = false;
	volatile AsyncResult<Void> depsFuture = null;
	volatile AsyncResult<Void> loadFuture = null;
	volatile T asset = null;

	volatile boolean cancel = false;

	static <T> AssetLoadingTask<T> obtain(AssetManager manager, AssetLoader<T, AssetLoaderParameters<T>> loader,
			AsyncCallback<T> callback, String fileName, Class<T> type, AssetLoaderParameters<T> params, int priority) {
		@SuppressWarnings("unchecked")
		AssetLoadingTask<T> task = SynchronizedPools.obtain(AssetLoadingTask.class);
		task.manager = manager;
		task.loader = loader;
		task.fileName = fileName.replaceAll("\\\\", "/");
		task.type = type;
		task.params = params;
		task.priority = priority;
		task.callback = callback;

		task.startTime = manager.log.getLevel() == Logger.DEBUG ? TimeUtils.nanoTime() : 0;
		return task;
	}

	static <T> AssetLoadingTask<T> obtain(AssetManager manager, AssetLoader<T, AssetLoaderParameters<T>> loader,
			DependencyCallback<T> dependency) {
		@SuppressWarnings("unchecked")
		AssetLoadingTask<T> task = SynchronizedPools.obtain(AssetLoadingTask.class);
		task.manager = manager;
		task.loader = loader;
		task.fileName = dependency.descriptor.fileName;
		task.type = dependency.descriptor.type;
		task.params = dependency.descriptor.params;
		task.priority = dependency.task.priority;
		task.callback = dependency;

		task.startTime = manager.log.getLevel() == Logger.DEBUG ? TimeUtils.nanoTime() : 0;
		return task;
	}

	/** Loads parts of the asset asynchronously if the loader is an {@link AsynchronousAssetLoader}. */
	@Override
	public Void call() throws Exception {
		AsynchronousAssetLoader<T, AssetLoaderParameters<T>> asyncLoader = (AsynchronousAssetLoader<T, AssetLoaderParameters<T>>) loader;
		FileHandle file = getFile();

		if (!dependenciesLoaded) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Array<AssetDescriptor<?>> descriptors = (Array) asyncLoader.getDependencies(fileName, file, params);
			if (descriptors == null || descriptors.size == 0) {
				asyncLoader.loadAsync(manager, fileName, file, params);
				asyncDone = true;
			} else {
				removeDuplicates(descriptors);
				initDependencies(descriptors);
				manager.injectDependencies(dependencies);
			}
		} else {
			asyncLoader.loadAsync(manager, fileName, file, params);
		}

		return null;
	}

	private void initDependencies(Array<AssetDescriptor<?>> descriptors) {
		for (int i = 0; i < descriptors.size; i++) {
			dependencies.add(DependencyCallback.obtain(this, descriptors.get(i)));
		}
	}

	boolean update() {
		if (loader instanceof SynchronousAssetLoader) {
			handleSyncLoader();
		} else {
			handleAsyncLoader();
		}
		return asset != null;
	}

	private void handleSyncLoader() {
		SynchronousAssetLoader<T, AssetLoaderParameters<T>> syncLoader = (SynchronousAssetLoader<T, AssetLoaderParameters<T>>) loader;
		FileHandle file = getFile();

		if (!dependenciesLoaded) {
			dependenciesLoaded = true;
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Array<AssetDescriptor<?>> descriptors = (Array) syncLoader.getDependencies(fileName, file, params);
			if (descriptors == null || descriptors.size == 0) {
				asset = syncLoader.load(manager, fileName, file, params);
				callback.onProgress(1);
				callback.onSuccess(asset);
			} else {
				removeDuplicates(descriptors);
				initDependencies(descriptors);
				manager.injectDependencies(dependencies);
			}
		} else {
			asset = syncLoader.load(manager, fileName, file, params);
			callback.onProgress(1);
			callback.onSuccess(asset);
		}
	}

	private void handleAsyncLoader() {
		AsynchronousAssetLoader<T, AssetLoaderParameters<T>> asyncLoader = (AsynchronousAssetLoader<T, AssetLoaderParameters<T>>) loader;

		if (!dependenciesLoaded) {
			if (depsFuture == null) {
				depsFuture = manager.executor.submit(this);
			} else if (depsFuture.isDone()) {
				try {
					depsFuture.get();
				} catch (Exception e) {
					throw new GdxRuntimeException("Couldn't load dependencies of asset: " + fileName, e);
				}

				dependenciesLoaded = true;
				if (asyncDone) {
					asset = asyncLoader.loadSync(manager, fileName, getFile(), params);
					callback.onProgress(1);
					callback.onSuccess(asset);
				}
			}
		} else {
			if (loadFuture == null && !asyncDone) {
				loadFuture = manager.executor.submit(this);
			} else if (asyncDone) {
				asset = asyncLoader.loadSync(manager, fileName, getFile(), params);
				callback.onProgress(1);
				callback.onSuccess(asset);
			} else if (loadFuture.isDone()) {
				try {
					loadFuture.get();
				} catch (Exception e) {
					throw new GdxRuntimeException("Couldn't load asset: " + fileName, e);
				}

				asset = asyncLoader.loadSync(manager, fileName, getFile(), params);
				callback.onProgress(1);
				callback.onSuccess(asset);
			}
		}
	}

	private FileHandle getFile() {
		if (file == null) {
			file = loader.resolve(fileName);
		}
		return file;
	}

	public T getAsset() {
		return asset;
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

	public void updateProgress() {
		if (!dependenciesLoaded) {
			callback.onProgress(0);
			return;
		}
		float progres = 0;
		int size = dependencies.size;
		float sizeInverted = 1.0f / size;

		for (int i = 0; i < size; i++) {
			DependencyCallback<?> dependency = dependencies.get(i);
			progres += dependency.progress * sizeInverted;
		}

		callback.onProgress(0.1f + (0.9f * progres));
	}

	@Override
	public void onSuccess(T value) {
		if (callback != null) {
			callback.onSuccess(value);
		}
	}

	@Override
	public void onException(Throwable exception) {
		if (callback != null) {
			callback.onException(exception);
		}
	}

	@Override
	public void onProgress(float progress) {
		if (callback != null) {
			callback.onProgress(progress);
		}
	}

	@Override
	public int compareTo(AssetLoadingTask<?> other) {
		return Integer.compare(priority, other.priority);
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
		asyncDone = false;
		dependenciesLoaded = false;
		depsFuture = null;
		loadFuture = null;
		asset = null;
		cancel = false;
		SynchronizedPools.freeAll(dependencies);
		dependencies.clear();
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

	static class DependencyCallback<T> implements AsyncCallback<T>, Poolable {
		AssetLoadingTask<?> task;
		private float progress;

		AssetDescriptor<T> descriptor;

		static <T> DependencyCallback<T> obtain(AssetLoadingTask<?> task, AssetDescriptor<T> descriptor) {
			@SuppressWarnings("unchecked")
			DependencyCallback<T> callback = SynchronizedPools.obtain(DependencyCallback.class);
			callback.task = task;
			callback.descriptor = descriptor;
			return callback;
		}

		@Override
		public void onSuccess(T value) {
			progress = 1;
			task.updateProgress();
		}

		@Override
		public void onException(Throwable exception) {
			progress = 1;
			task.onException(exception);
		}

		@Override
		public void onProgress(float progress) {
			this.progress = 1;
			task.updateProgress();
		}

		@Override
		public void reset() {
			task = null;
			progress = 0;
			descriptor = null;
		}

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append(descriptor.fileName);
			buffer.append(" ");
			buffer.append(descriptor.type.getName());
			return buffer.toString();
		}
	}
}
