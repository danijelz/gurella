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
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;

class AssetLoadingTask<T, P extends AssetLoaderParameters<T>> implements AsyncTask<Void> {
	AssetManager manager;
	final AssetDescriptor<T> assetDesc;
	final AssetLoader<T, P> loader;
	final AsyncExecutor executor;
	final long startTime;

	volatile boolean asyncDone = false;
	volatile boolean dependenciesLoaded = false;
	volatile Array<AssetDescriptor<?>> dependencies;
	volatile AsyncResult<Void> depsFuture = null;
	volatile AsyncResult<Void> loadFuture = null;
	volatile T asset = null;

	int ticks = 0;
	volatile boolean cancel = false;

	public AssetLoadingTask(AssetManager manager, AssetDescriptor<T> assetDesc, AssetLoader<T, P> loader,
			AsyncExecutor threadPool) {
		this.manager = manager;
		this.assetDesc = assetDesc;
		this.loader = loader;
		this.executor = threadPool;
		startTime = manager.log.getLevel() == Logger.DEBUG ? TimeUtils.nanoTime() : 0;
	}

	/** Loads parts of the asset asynchronously if the loader is an {@link AsynchronousAssetLoader}. */
	@Override
	public Void call() throws Exception {
		AsynchronousAssetLoader<T, P> asyncLoader = (AsynchronousAssetLoader<T, P>) loader;
		@SuppressWarnings("unchecked")
		P params = (P) assetDesc.params;

		if (dependenciesLoaded == false) {
			dependencies = (Array) asyncLoader.getDependencies(assetDesc.fileName, resolve(loader, assetDesc), params);
			if (dependencies != null) {
				removeDuplicates(dependencies);
				manager.injectDependencies(assetDesc.fileName, dependencies);
			} else {
				// if we have no dependencies, we load the async part of the task immediately.
				asyncLoader.loadAsync(manager, assetDesc.fileName, resolve(loader, assetDesc), params);
				asyncDone = true;
			}
		} else {
			asyncLoader.loadAsync(manager, assetDesc.fileName, resolve(loader, assetDesc), params);
		}
		return null;
	}

	/**
	 * Updates the loading of the asset. In case the asset is loaded with an {@link AsynchronousAssetLoader}, the
	 * loaders {@link AsynchronousAssetLoader#loadAsync(AssetManager, String, FileHandle, AssetLoaderParameters)} method
	 * is first called on a worker thread. Once this method returns, the rest of the asset is loaded on the rendering
	 * thread via {@link AsynchronousAssetLoader#loadSync(AssetManager, String, FileHandle, AssetLoaderParameters)}.
	 * 
	 * @return true in case the asset was fully loaded, false otherwise
	 * @throws GdxRuntimeException
	 */
	public boolean update() {
		ticks++;
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
		P params = (P) assetDesc.params;

		if (!dependenciesLoaded) {
			dependenciesLoaded = true;
			dependencies = (Array) syncLoader.getDependencies(assetDesc.fileName, resolve(loader, assetDesc), params);
			if (dependencies == null) {
				asset = syncLoader.load(manager, assetDesc.fileName, resolve(loader, assetDesc), params);
				return;
			}
			removeDuplicates(dependencies);
			manager.injectDependencies(assetDesc.fileName, dependencies);
		} else {
			asset = syncLoader.load(manager, assetDesc.fileName, resolve(loader, assetDesc), params);
		}
	}

	private void handleAsyncLoader() {
		AsynchronousAssetLoader<T, P> asyncLoader = (AsynchronousAssetLoader<T, P>) loader;
		@SuppressWarnings("unchecked")
		P params = (P) assetDesc.params;

		if (!dependenciesLoaded) {
			if (depsFuture == null) {
				depsFuture = executor.submit(this);
			} else {
				if (depsFuture.isDone()) {
					try {
						depsFuture.get();
					} catch (Exception e) {
						throw new GdxRuntimeException("Couldn't load dependencies of asset: " + assetDesc.fileName, e);
					}
					dependenciesLoaded = true;
					if (asyncDone) {
						asset = asyncLoader.loadSync(manager, assetDesc.fileName, resolve(loader, assetDesc), params);
					}
				}
			}
		} else {
			if (loadFuture == null && !asyncDone) {
				loadFuture = executor.submit(this);
			} else {
				if (asyncDone) {
					asset = asyncLoader.loadSync(manager, assetDesc.fileName, resolve(loader, assetDesc), params);
				} else if (loadFuture.isDone()) {
					try {
						loadFuture.get();
					} catch (Exception e) {
						throw new GdxRuntimeException("Couldn't load asset: " + assetDesc.fileName, e);
					}
					asset = asyncLoader.loadSync(manager, assetDesc.fileName, resolve(loader, assetDesc), params);
				}
			}
		}
	}

	private static FileHandle resolve(AssetLoader<?, ?> loader, AssetDescriptor<?> assetDesc) {
		if (assetDesc.file == null)
			assetDesc.file = loader.resolve(assetDesc.fileName);
		return assetDesc.file;
	}

	public T getAsset() {
		return asset;
	}

	private static void removeDuplicates(Array<AssetDescriptor<?>> array) {
		boolean ordered = array.ordered;
		array.ordered = true;
		for (int i = 0; i < array.size; ++i) {
			final String fn = array.get(i).fileName;
			final Class<?> type = array.get(i).type;
			for (int j = array.size - 1; j > i; --j) {
				if (type == array.get(j).type && fn.equals(array.get(j).fileName))
					array.removeIndex(j);
			}
		}
		array.ordered = ordered;
	}

}
