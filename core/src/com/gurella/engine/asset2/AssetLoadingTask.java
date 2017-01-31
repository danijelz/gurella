package com.gurella.engine.asset2;

import static com.gurella.engine.asset2.AssetLoadingState.asyncLoading;
import static com.gurella.engine.asset2.AssetLoadingState.finished;
import static com.gurella.engine.asset2.AssetLoadingState.ready;
import static com.gurella.engine.asset2.AssetLoadingState.syncLoading;
import static com.gurella.engine.asset2.AssetLoadingState.waitingDependencies;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.utils.Values;

class AssetLoadingTask<A, T> implements Comparable<AssetLoadingTask<?, ?>>, Poolable {
	private static int requestSequence = Integer.MIN_VALUE;

	int priority;
	int requestId;

	final AssetId assetId = new AssetId();
	final AssetDependencies dependencies = new AssetDependencies();
	final DelegatingCallback<T> callback = new DelegatingCallback<T>();

	AssetLoadingTask<A, T> parent;

	AssetsManager manager;
	FileHandle file;
	Class<T> assetType;

	AssetLoader<A, T, AssetProperties<T>> loader;
	AssetProperties<T> properties;

	volatile AssetLoadingState state = ready;
	volatile float progress = 0;

	A asyncData;
	T asset;
	Throwable exception;

	void init(AssetsManager manager, FileHandle file, Class<T> assetType, AsyncCallback<T> callback, int priority) {
		this.manager = manager;
		this.file = file;
		this.assetType = assetType;
		this.callback.delegate = callback;
		this.priority = priority;

		requestId = requestSequence++;
		assetId.set(file, assetType);
		loader = resolveLoader();
		dependencies.init(assetId, manager);
	}

	private AssetLoader<A, T, AssetProperties<T>> resolveLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	void init(AssetLoadingTask<A, T> parent, FileHandle file, Class<T> assetType, int priority) {
		this.parent = parent;
		this.manager = parent.manager;
		this.file = file;
		this.assetType = assetType;
		this.priority = priority;

		requestId = requestSequence++;
		assetId.set(file, assetType);
		loader = resolveLoader();
		dependencies.init(assetId, manager);
	}

	void update() {
		boolean doneStepping = false;
		while (!doneStepping) {
			try {
				doneStepping = step();
			} catch (Exception exception) {
				this.exception = exception;
				state = finished;
				doneStepping = true;
			} finally {
				// TODO handle exceptions
				updateProgress();
			}
		}
	}

	private boolean step() {
		switch (state) {
		case ready:
			state = start();
			return state == waitingDependencies;
		case asyncLoading:
			properties = dependencies.getProperties();
			asyncData = loader.loadAsync(dependencies, file, properties);
			state = syncLoading;
			return true;
		case syncLoading:
			asset = loader.consumeAsyncData(dependencies, file, properties, asyncData);
			state = syncLoading;
			return true;
		default:
			this.exception = new IllegalStateException("Invalid loading state.");
			state = finished;
			return true;
		}
	}

	private AssetLoadingState start() {
		loader.initDependencies(dependencies, file);
		FileHandle propsHandle = Assets.getPropertiesFile(assetId.fileName, assetId.fileType, assetId.assetType);
		if (propsHandle != null) {
			dependencies.addDependency(propsHandle.path(), propsHandle.type(), AssetProperties.class);
		}
		return dependencies.allResolved() ? asyncLoading : waitingDependencies;
	}

	void updateProgress() {
		switch (state) {
		case ready:
			notifyProgress(0);
			break;
		case waitingDependencies:
			float depProgress = dependencies.getProgress();
			notifyProgress(0.8f * depProgress);
			if (depProgress == 1) {
				state = asyncLoading;
				// TODO notify state changes
			}
			break;
		case asyncLoading:
			notifyProgress(0.8f);
			break;
		case syncLoading:
			notifyProgress(0.9f);
			break;
		case finished:
			notifyProgress(1);
			break;
		default:
			throw new IllegalStateException();
		}
	}

	private void notifyProgress(float progress) {
		if (this.progress == progress) {
			return;
		}

		this.progress = progress;
		if (parent != null) {
			parent.updateProgress();
		}

		callback.onProgress(progress);
	}

	void merge(AsyncCallback<T> concurrentCallback, int newPriority) {
		callback.concurrentCallbacks.add(concurrentCallback);
		if (priority < newPriority) {
			renice(requestSequence++, newPriority);
		}
		concurrentCallback.onProgress(progress);
	}

	private void renice(int newRequestId, int newPriority) {
		requestId = newRequestId;
		priority = newPriority;

		for (int i = 0; i < dependencies.size; i++) {
			AssetLoadingTask<?, ?> dependency = dependencies.get(i);
			dependency.renice(newRequestId, newPriority);
		}
	}

	void finish() {
		if (exception == null) {
			callback.onSuccess(asset);
		} else {
			callback.onException(exception);
		}

		dependencies.unreserveDependencies(exception != null);
	}

	@Override
	public int compareTo(AssetLoadingTask<?, ?> other) {
		int result = Values.compare(priority, other.priority);
		return result == 0 ? Values.compare(requestId, other.requestId) : result;
	}

	@Override
	public void reset() {
		parent = null;
		manager = null;
		file = null;
		assetType = null;
		loader = null;
		properties = null;
		asyncData = null;
		asset = null;
		exception = null;
		priority = 0;
		requestId = 0;
		state = ready;
		progress = 0;
		assetId.reset();
		dependencies.reset();
		callback.reset();
	}

	private static class DelegatingCallback<T> implements AsyncCallback<T> {
		private AsyncCallback<T> delegate;
		private final Array<AsyncCallback<T>> concurrentCallbacks = new Array<AsyncCallback<T>>();

		@Override
		public void onSuccess(T value) {
			if (delegate != null) {
				delegate.onSuccess(value);
			}
			for (int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onSuccess(value);
			}
		}

		@Override
		public void onException(Throwable exception) {
			if (delegate != null) {
				delegate.onException(exception);
			}
			for (int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onException(exception);
			}
		}

		@Override
		public void onCanceled(String message) {
			if (delegate != null) {
				delegate.onCanceled(message);
			}
			for (int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onCanceled(message);
			}
		}

		@Override
		public void onProgress(float progress) {
			if (delegate != null) {
				delegate.onProgress(progress);
			}
			for (int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onProgress(progress);
			}
		}

		void reset() {
			delegate = null;
			concurrentCallbacks.clear();
		}
	}
}
