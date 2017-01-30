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
	final AssetId assetId = new AssetId();// TODO init
	final AssetDependencies dependencies = new AssetDependencies();
	final DelegatingCallback<T> callback = new DelegatingCallback<T>();

	AssetLoadingTask<A, T> parent;

	FileHandle file;
	AssetLoader<A, T, AssetProperties<T>> loader;
	AssetProperties<T> properties;

	volatile float progress = 0;
	volatile AssetLoadingState state = ready;

	A asyncData;
	T asset;
	Throwable exception;

	void init(AssetLoader<A, T, AssetProperties<T>> loader, FileHandle file, AsyncCallback<T> callback, int priority) {
		requestId = requestSequence++;
		this.loader = loader;
		this.file = file;
		this.callback.delegate = callback;
		this.priority = priority;
	}

	void init(AssetLoadingTask<A, T> parent, AssetLoader<A, T, AssetProperties<T>> loader, FileHandle file,
			AsyncCallback<T> callback, int priority) {
		requestId = requestSequence++;
		this.parent = parent;
		this.loader = loader;
		this.file = file;
		this.callback.delegate = callback;
		this.priority = priority;
	}

	void process() {
		try {
			processSafely();
		} catch (Exception exception) {
			this.exception = exception;
			state = finished;
		} finally {
			updateProgress();
		}
	}

	private void processSafely() {
		switch (state) {
		case ready:
			initDependencies();
			if (dependencies.isEmpty()) {
				state = asyncLoading;
				processSafely();
			} else {
				state = waitingDependencies;
			}
			break;
		case asyncLoading:
			properties = dependencies.getProperties();
			asyncData = loader.loadAsync(dependencies, file, properties);
			state = syncLoading;
			break;
		case syncLoading:
			asset = loader.consumeAsyncData(dependencies, file, properties, asyncData);
			state = syncLoading;
			break;
		default:
			this.exception = new IllegalStateException("Invalid loading state.");
			state = finished;
		}
	}

	private void initDependencies() {
		loader.initDependencies(dependencies, file);
		if (properties != null) {
			return;
		}

		FileHandle propsHandle = Assets.getPropertiesFile(assetId.fileName, assetId.fileType, assetId.assetType);
		if (propsHandle == null) {
			return;
		}

		dependencies.addDependency(propsHandle.path(), assetId.fileType, AssetProperties.class);
	}

	void consumeAsyncData() {
		try {
			asset = loader.consumeAsyncData(dependencies, file, properties, asyncData);
		} catch (Exception e) {
			exception = e;
		} finally {
			state = finished;
			updateProgress();
		}
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
		this.progress = progress;
		if (parent != null) {
			parent.updateProgress();
		}

		callback.onProgress(progress);
	}

	public void merge(AsyncCallback<T> concurrentCallback, int newPriority) {
		callback.concurrentCallbacks.add(concurrentCallback);
		if (priority < newPriority) {
			reniceHierarchy(requestSequence++, newPriority);
		}
		concurrentCallback.onProgress(progress);
	}

	private void reniceHierarchy(int newRequestId, int newPriority) {
		requestId = newRequestId;
		priority = newPriority;

		for (int i = 0; i < dependencies.size; i++) {
			AssetLoadingTask<?, ?> dependency = dependencies.get(i);
			dependency.reniceHierarchy(newRequestId, newPriority);
		}
	}

	void notifyFinished() {
		if (exception == null) {
			callback.onSuccess(asset);
		} else {
			callback.onException(exception);
		}
	}

	@Override
	public int compareTo(AssetLoadingTask<?, ?> other) {
		int result = Values.compare(priority, other.priority);
		return result == 0 ? Values.compare(requestId, other.requestId) : result;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}

	private static class DelegatingCallback<T> implements AsyncCallback<T> {
		private AsyncCallback<T> delegate;
		private Array<AsyncCallback<T>> concurrentCallbacks = new Array<AsyncCallback<T>>();

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
		public void onCancled(String message) {
			if (delegate != null) {
				delegate.onCancled(message);
			}
			for (int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onCancled(message);
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
	}
}
