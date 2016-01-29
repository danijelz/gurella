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

class AssetLoadingTask<T, P extends AssetLoaderParameters<T>>
		implements AsyncTask<Void>, Comparable<AssetLoadingTask<?, ?>>, Poolable {
	AssetManager manager;
	CallbackDescriptor<T> descriptor;
	AssetLoader<T, P> loader;
	long startTime;

	final Array<DependencyCallback<?>> dependencies = new Array<DependencyCallback<?>>();

	volatile boolean asyncDone = false;
	volatile boolean dependenciesLoaded = false;
	volatile AsyncResult<Void> depsFuture = null;
	volatile AsyncResult<Void> loadFuture = null;
	volatile T asset = null;

	volatile boolean cancel = false;

	static <T, P extends AssetLoaderParameters<T>> AssetLoadingTask<T, P> obtain(AssetManager manager,
			CallbackDescriptor<T> descriptor, AssetLoader<T, P> loader) {
		@SuppressWarnings("unchecked")
		AssetLoadingTask<T, P> task = SynchronizedPools.obtain(AssetLoadingTask.class);
		task.manager = manager;
		task.descriptor = descriptor;
		task.loader = loader;
		task.startTime = manager.log.getLevel() == Logger.DEBUG ? TimeUtils.nanoTime() : 0;
		return task;
	}

	/** Loads parts of the asset asynchronously if the loader is an {@link AsynchronousAssetLoader}. */
	@Override
	public Void call() throws Exception {
		AsynchronousAssetLoader<T, P> asyncLoader = (AsynchronousAssetLoader<T, P>) loader;
		@SuppressWarnings("unchecked")
		P params = (P) descriptor.params;
		FileHandle file = resolve(loader, descriptor);
		String fileName = descriptor.fileName;

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
		String parentFilename = descriptor.fileName;
		int priority = descriptor.priority;
		for (int i = 0; i < descriptors.size; i++) {
			dependencies.add(DependencyCallback.obtain(this, parentFilename, descriptors.get(i), priority));
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
		SynchronousAssetLoader<T, P> syncLoader = (SynchronousAssetLoader<T, P>) loader;
		@SuppressWarnings("unchecked")
		P params = (P) descriptor.params;
		FileHandle file = resolve(loader, descriptor);
		String fileName = descriptor.fileName;

		if (!dependenciesLoaded) {
			dependenciesLoaded = true;
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Array<AssetDescriptor<?>> descriptors = (Array) syncLoader.getDependencies(fileName, file, params);
			if (descriptors == null || descriptors.size == 0) {
				asset = syncLoader.load(manager, fileName, file, params);
				descriptor.onProgress(1);
				descriptor.onSuccess(asset);
			} else {
				removeDuplicates(descriptors);
				initDependencies(descriptors);
				manager.injectDependencies(dependencies);
			}
		} else {
			asset = syncLoader.load(manager, fileName, file, params);
			descriptor.onProgress(1);
			descriptor.onSuccess(asset);
		}
	}

	private void handleAsyncLoader() {
		AsynchronousAssetLoader<T, P> asyncLoader = (AsynchronousAssetLoader<T, P>) loader;
		@SuppressWarnings("unchecked")
		P params = (P) descriptor.params;
		String fileName = descriptor.fileName;

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
				FileHandle file = resolve(loader, descriptor);
				if (asyncDone) {
					asset = asyncLoader.loadSync(manager, fileName, file, params);
					descriptor.onProgress(1);
					descriptor.onSuccess(asset);
				}
			}
		} else {
			if (loadFuture == null && !asyncDone) {
				loadFuture = manager.executor.submit(this);
			} else if (asyncDone) {
				FileHandle file = resolve(loader, descriptor);
				asset = asyncLoader.loadSync(manager, fileName, file, params);
				descriptor.onProgress(1);
				descriptor.onSuccess(asset);
			} else if (loadFuture.isDone()) {
				try {
					loadFuture.get();
				} catch (Exception e) {
					throw new GdxRuntimeException("Couldn't load asset: " + fileName, e);
				}

				FileHandle file = resolve(loader, descriptor);
				asset = asyncLoader.loadSync(manager, fileName, file, params);
				descriptor.onProgress(1);
				descriptor.onSuccess(asset);
			}
		}
	}

	private static FileHandle resolve(AssetLoader<?, ?> loader, CallbackDescriptor<?> descriptor) {
		if (descriptor.file == null) {
			descriptor.file = loader.resolve(descriptor.fileName);
		}
		return descriptor.file;
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

	void free() {
		SynchronizedPools.free(this);
	}

	public void updateProgress() {
		if (!dependenciesLoaded) {
			descriptor.onProgress(0);
			return;
		}
		float progres = 0;
		int size = dependencies.size;
		float sizeInverted = 1.0f / size;

		for (int i = 0; i < size; i++) {
			DependencyCallback<?> dependency = dependencies.get(i);
			progres += dependency.progress * sizeInverted;
		}

		descriptor.onProgress(0.1f + (0.9f * progres));
	}

	@Override
	public void reset() {
		manager = null;
		descriptor.free();
		descriptor = null;
		loader = null;
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

	@Override
	public int compareTo(AssetLoadingTask<?, ?> other) {
		return Integer.compare(descriptor.priority, other.descriptor.priority);
	}

	static class DependencyCallback<T> implements AsyncCallback<T>, Poolable {
		private AssetLoadingTask<?, ?> task;
		private float progress;

		String parentFilename;
		AssetDescriptor<T> descriptor;
		int priority;

		static <T> DependencyCallback<T> obtain(AssetLoadingTask<?, ?> task, String parentFilename,
				AssetDescriptor<T> descriptor, int priority) {
			@SuppressWarnings("unchecked")
			DependencyCallback<T> callback = SynchronizedPools.obtain(DependencyCallback.class);
			callback.task = task;
			callback.parentFilename = parentFilename;
			callback.descriptor = descriptor;
			callback.priority = priority;
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
			task.descriptor.onException(exception);
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
			parentFilename = null;
			priority = 0;
		}
	}
}
