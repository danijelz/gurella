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

class AssetLoadingTask<T> implements AsyncTask<Void>, Comparable<AssetLoadingTask<?>>, Poolable {
	private static int counter = Integer.MIN_VALUE;

	int loadRequestId;
	int priority;

	AssetManager manager;
	AssetLoader<T, AssetLoaderParameters<T>> loader;
	AsyncCallback<T> callback;

	String fileName;
	FileHandle file;
	Class<T> type;
	AssetLoaderParameters<T> params;

	AssetLoadingTask<?> parent;
	final Array<AssetLoadingTask<?>> dependencies = new Array<AssetLoadingTask<?>>();
	final Array<AssetLoadingTask<T>> concurentTasks = new Array<AssetLoadingTask<T>>();

	volatile float progress = 0;
	LoadingState loadingState = LoadingState.ready;

	AssetReference reference;
	Throwable exception;

	static <T> AssetLoadingTask<T> obtain(AssetManager manager, AsyncCallback<T> callback, String fileName,
			Class<T> type, AssetLoaderParameters<T> params, int priority, boolean sticky) {
		@SuppressWarnings("unchecked")
		AssetLoadingTask<T> task = SynchronizedPools.obtain(AssetLoadingTask.class);
		task.manager = manager;
		task.loader = manager.findLoader(type, fileName);
		task.fileName = fileName.replaceAll("\\\\", "/");
		task.type = type;
		task.params = params;
		task.priority = priority;
		task.callback = callback;
		task.loadRequestId = counter++;
		task.reference = AssetReference.obtain();
		task.reference.incRefCount();
		task.reference.sticky = sticky;
		return task;
	}

	static <T> AssetLoadingTask<T> obtain(AssetLoadingTask<?> parent, String fileName, FileHandle file, Class<T> type,
			AssetLoaderParameters<T> params) {
		@SuppressWarnings("unchecked")
		AssetLoadingTask<T> task = SynchronizedPools.obtain(AssetLoadingTask.class);
		task.manager = parent.manager;
		task.loader = parent.manager.findLoader(type, fileName);
		task.fileName = fileName;
		task.file = file;
		task.type = type;
		task.params = params;
		task.priority = parent.priority;
		task.parent = parent;
		task.loadRequestId = parent.loadRequestId;
		task.reference = AssetReference.obtain();
		task.reference.addDependent(parent.fileName);
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
			initDependencies(descriptors);
			manager.waitingForDependencies(this);
		}
	}

	private void initDependencies(Array<AssetDescriptor<?>> descriptors) {
		for (int i = 0; i < descriptors.size; i++) {
			AssetDescriptor<Object> descriptor = ValueUtils.cast(descriptors.get(i));
			AssetLoaderParameters<Object> castedParams = ValueUtils.cast(descriptor.params);
			Class<Object> dependencyType = descriptor.type;
			String dependencyFileName = descriptor.fileName;

			AssetLoadingTask<?> duplicate = findDuplicate(dependencyFileName);
			if (duplicate == null) {
				reference.addDependency(dependencyFileName);
				dependencies.add(obtain(this, dependencyFileName, descriptor.file, dependencyType, castedParams));
			} else if (dependencyType != duplicate.type) {
				throw new GdxRuntimeException("Dependencies conflict.");
			}
		}
	}

	private AssetLoadingTask<?> findDuplicate(String otherFileName) {
		for (int i = 0; i < dependencies.size; i++) {
			AssetLoadingTask<?> dependency = dependencies.get(i);
			if (dependency.fileName.equals(otherFileName)) {
				return dependency;
			}
		}
		return null;
	}

	private void loadAsync() {
		if (loader instanceof SynchronousAssetLoader) {
			SynchronousAssetLoader<T, AssetLoaderParameters<T>> syncLoader = ValueUtils.cast(loader);
			reference.asset = syncLoader.load(manager, fileName, file, params);
			manager.finished(this);
		} else {
			AsynchronousAssetLoader<T, AssetLoaderParameters<T>> asyncLoader = ValueUtils.cast(loader);
			asyncLoader.loadAsync(manager, fileName, file, params);
			manager.readyForSyncLoading(this);
		}
	}

	void loadSync() {
		AsynchronousAssetLoader<T, AssetLoaderParameters<T>> asyncLoader = ValueUtils.cast(loader);
		reference.asset = asyncLoader.loadSync(manager, fileName, file, params);
	}

	void updateProgress() {
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

	private void notifyProgress(float progress) {
		this.progress = progress;
		if (parent != null) {
			parent.updateProgress();
		} else if (callback != null) {
			callback.onProgress(progress);
		}

		for (int i = 0; i < concurentTasks.size; i++) {
			concurentTasks.get(i).notifyProgress(progress);
		}
	}

	private float getDependenciesProgress() {
		int size = dependencies.size;
		if (size == 0) {
			return 1;
		}

		float progres = 0;
		for (int i = 0; i < size; i++) {
			AssetLoadingTask<?> dependency = dependencies.get(i);
			progres += dependency.progress;
		}

		return Math.min(1, progres / size);
	}

	void merge(AssetLoadingTask<T> concurentTask) {
		concurentTasks.add(concurentTask);

		reference.sticky |= concurentTask.reference.sticky;
		AssetLoadingTask<?> concurrentTaskParent = concurentTask.parent;
		if (concurrentTaskParent == null) {
			reference.incRefCount();
		} else {
			reference.addDependent(concurrentTaskParent.fileName);
		}

		int newPriority = concurentTask.priority;
		if (priority < newPriority) {
			reniceHierarchy(concurentTask.loadRequestId, newPriority);
		}
	}

	private void reniceHierarchy(int newLoadRequestId, int newPriority) {
		loadRequestId = newLoadRequestId;
		priority = newPriority;
		for (int i = 0; i < dependencies.size; i++) {
			AssetLoadingTask<?> dependency = dependencies.get(i);
			dependency.reniceHierarchy(newLoadRequestId, newPriority);
		}
	}

	void setLoadingState(LoadingState loadingState) {
		this.loadingState = loadingState;
		for (int i = 0; i < concurentTasks.size; i++) {
			concurentTasks.get(i).loadingState = loadingState;
		}
	}

	@Override
	public int compareTo(AssetLoadingTask<?> other) {
		int result = Integer.compare(other.priority, priority);
		return result == 0 ? Long.compare(loadRequestId, other.loadRequestId) : result;
	}

	@Override
	public void reset() {
		loadRequestId = Integer.MAX_VALUE;
		priority = 0;
		manager = null;
		loader = null;
		callback = null;
		fileName = null;
		file = null;
		type = null;
		params = null;
		parent = null;

		SynchronizedPools.freeAll(dependencies);
		dependencies.clear();

		for (int i = 0; i < concurentTasks.size; i++) {
			concurentTasks.get(i).free();
		}
		concurentTasks.clear();

		progress = 0;
		loadingState = LoadingState.ready;

		exception = null;
		if (reference != null) {
			reference.free();
			reference = null;
		}
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
