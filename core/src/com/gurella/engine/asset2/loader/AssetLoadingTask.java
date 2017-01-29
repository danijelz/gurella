package com.gurella.engine.asset2.loader;

import static com.gurella.engine.asset2.loader.AssetLoadingState.error;
import static com.gurella.engine.asset2.loader.AssetLoadingState.finished;
import static com.gurella.engine.asset2.loader.AssetLoadingState.syncLoading;
import static com.gurella.engine.asset2.loader.AssetLoadingState.waitingDependencies;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.asset2.AssetId;
import com.gurella.engine.asset2.Assets;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.utils.Values;

class AssetLoadingTask<A, T> implements AsyncTask<Void>, Comparable<AssetLoadingTask<?, ?>>, Poolable {
	private static int requestIdIndex = Integer.MIN_VALUE;

	int priority;
	int requestId;
	final AssetId assetId = new AssetId();
	final AssetDependencies dependencies = new AssetDependencies();
	final DelegatingCallback<T> callback = new DelegatingCallback<T>();

	AssetsLoader executor;
	AssetLoadingTask<A, T> parent;

	FileHandle file;
	AssetLoader<A, T, AssetProperties<T>> loader;
	AssetProperties<T> props;

	volatile float progress = 0;
	volatile AssetLoadingState state = AssetLoadingState.ready;

	A asyncData;
	T asset;
	Throwable exception;

	void init(AssetsLoader executor, AssetLoader<A, T, AssetProperties<T>> loader, FileHandle file,
			AsyncCallback<T> callback, int priority) {
		requestId = requestIdIndex++;
		this.executor = executor;
		this.loader = loader;
		this.file = file;
		this.callback.delegate = callback;
		this.priority = priority;
	}

	void init(AssetLoadingTask<A, T> parent, AssetLoader<A, T, AssetProperties<T>> loader, FileHandle file,
			AsyncCallback<T> callback, int priority) {
		requestId = requestIdIndex++;
		this.parent = parent;
		this.executor = parent.executor;
		this.loader = loader;
		this.file = file;
		this.callback.delegate = callback;
		this.priority = priority;
	}

	@Override
	public Void call() throws Exception {
		try {
			switch (state) {
			case ready:
				start();
				break;
			case asyncLoading:
				loadAsync();
				break;
			default:
				throw new IllegalStateException();
			}
		} catch (Exception exception) {
			this.exception = exception;
			state = error;
		} finally {
			updateProgress();
		}

		return null;
	}

	private void start() {
		loader.initDependencies(dependencies, file);
		initProps();

		if (dependencies.isEmpty()) {
			loadAsync();
		} else {
			initDependencies();
			state = waitingDependencies;
			executor.waitingDependencies(this);
		}
	}

	private void initProps() {
		if (props != null) {
			return;
		}

		FileHandle propsHandle = Assets.getPropertiesFile(assetId.fileName, assetId.fileType, assetId.assetType);
		if (propsHandle == null) {
			return;
		}

		dependencies.addDependency(propsHandle.path(), assetId.fileType, AssetProperties.class);
	}

	private void loadAsync() {
		asyncData = loader.deserializeAsync(dependencies, file, props);
		state = syncLoading;
		executor.loadSync(this);
	}

	private void initDependencies() {
		for (int i = 0; i < descriptors.size; i++) {
			AssetLoaderParameters<Object> castedParams = Values.cast(descriptor.params);
			Class<Object> dependencyType = descriptor.type;
			String dependencyFileName = descriptor.fileName;

			AssetLoadingTask<?, ?> duplicate = findDuplicate(dependencyFileName);
			if (duplicate == null) {
				info.addDependency(dependencyFileName);
				dependencies.add(obtain(this, dependencyFileName, descriptor.file, dependencyType, castedParams));
			} else if (dependencyType != duplicate.type) {
				throw new GdxRuntimeException("Dependencies conflict.");
			}
		}
	}

	void consumeAsyncData() {
		try {
			asset = loader.consumeAsyncData(dependencies, file, props, asyncData);
			state = finished;
		} catch (Exception e) {
			exception = e;
			state = error;
		} finally {
			updateProgress();
		}
	}

	int updateProgress() {
		switch (state) {
		case ready:
			notifyProgress(0);
			break;
		case waitingDependencies:
			float progress = dependencies.getDependenciesProgress();
			notifyProgress(0.8f * progress);
			if (progress == 1) {
				executor.loadAsync(this);
			}
			break;
		case asyncLoading:
			notifyProgress(0.8f);
			break;
		case syncLoading:
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
		} else {
			callback.onProgress(progress);
		}

		for (int i = 0; i < concurentTasks.size; i++) {
			concurentTasks.get(i).notifyProgress(progress);
		}
	}

	public void merge(AsyncCallback<T> concurrentCallback, int newPriority) {
		callback.concurrentCallbacks.add(concurrentCallback);
		if (priority < newPriority) {
			reniceHierarchy(requestIdIndex++, newPriority);
		}
	}

	private void reniceHierarchy(int newRequestId, int newPriority) {
		requestId = newRequestId;
		priority = newPriority;
		for (int i = 0; i < dependencies.size; i++) {
			AssetLoadingTask<?, ?> dependency = dependencies.get(i);
			dependency.reniceHierarchy(newRequestId, newPriority);
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}

	@Override
	public int compareTo(AssetLoadingTask<?, ?> other) {
		int result = Values.compare(other.priority, priority);
		return result == 0 ? Values.compare(requestId, other.requestId) : result;
	}

	private static class DelegatingCallback<T> implements AsyncCallback<T> {
		private AsyncCallback<T> delegate;
		private Array<AsyncCallback<T>> concurrentCallbacks = new Array<AsyncCallback<T>>();

		@Override
		public void onSuccess(T value) {
			delegate.onSuccess(value);
			for (int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onSuccess(value);
			}
		}

		@Override
		public void onException(Throwable exception) {
			delegate.onException(exception);
			for (int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onException(exception);
			}
		}

		@Override
		public void onCancled(String message) {
			delegate.onCancled(message);
			for (int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onCancled(message);
			}
		}

		@Override
		public void onProgress(float progress) {
			delegate.onProgress(progress);
			for (int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onProgress(progress);
			}
		}
	}
}
