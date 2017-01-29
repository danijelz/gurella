package com.gurella.engine.asset2.loader;

import static com.gurella.engine.asset2.loader.AssetLoadingState.error;
import static com.gurella.engine.asset2.loader.AssetLoadingState.*;

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
	private static int counter = Integer.MIN_VALUE;

	int priority;
	int requestId;
	final AssetId assetId = new AssetId();
	final AssetDependencies dependencies = new AssetDependencies();

	AssetLoader executor;
	AssetLoadingTask<A, T> parent;

	FileHandle file;
	AssetDeserializer<A, T, AssetProperties<T>> deserializer;
	AssetProperties<T> props;
	AsyncCallback<T> callback;

	volatile float progress = 0;
	volatile AssetLoadingState state = AssetLoadingState.ready;

	A asyncData;
	T asset;
	Throwable exception;

	void init(AssetLoader executor, AssetDeserializer<A, T, AssetProperties<T>> deserializer, FileHandle file,
			AsyncCallback<T> callback, int priority) {
		requestId = counter++;
		this.executor = executor;
		this.deserializer = deserializer;
		this.file = file;
		this.callback = callback;
		this.priority = priority;
	}

	void init(AssetLoadingTask<A, T> parent, AssetDeserializer<A, T, AssetProperties<T>> deserializer, FileHandle file,
			AsyncCallback<T> callback, int priority) {
		requestId = counter++;
		this.parent = parent;
		this.executor = parent.executor;
		this.deserializer = deserializer;
		this.file = file;
		this.callback = callback;
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
		deserializer.injectDependencies(dependencies, file);
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
		asyncData = deserializer.deserializeAsync(dependencies, file, props);
		state = syncLoading;
		//TODO executor.loadSync(this);
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
			asset = deserializer.consumeAsyncData(dependencies, file, props, asyncData);
			state = finished;
		} catch (Exception e) {
			exception = e;
			state = error;
		} finally {
			updateProgress();
		}
	}

	void updateProgress() {
		switch (state) {
		case ready:
			notifyProgress(0);
			break;
		case waitingDependencies:
			float progress = dependencies.getDependenciesProgress();
			notifyProgress(0.8f * progress);
			if (progress == 1) {
				//TODO executor.loadAsync(this);
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

	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}

	@Override
	public int compareTo(AssetLoadingTask<?, ?> other) {
		int result = Values.compare(other.priority, priority);
		return result == 0 ? Values.compare(requestId, other.requestId) : result;
	}
	
	private static class TaskCallback<T> implements AsyncCallback<T> {
		private AsyncCallback<T> clientCallback;
		private Array<AsyncCallback<T>> concurrentCallbacks = new Array<AsyncCallback<T>>();
		
		@Override
		public void onSuccess(T value) {
			clientCallback.onSuccess(value);
			for(int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onSuccess(value);
			}
		}

		@Override
		public void onException(Throwable exception) {
			clientCallback.onException(exception);
			for(int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onException(exception);
			}
		}

		@Override
		public void onCancled(String message) {
			clientCallback.onCancled(message);
			for(int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onCancled(message);
			}
		}

		@Override
		public void onProgress(float progress) {
			clientCallback.onProgress(progress);
			for(int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onProgress(progress);
			}
		}
	}
}
