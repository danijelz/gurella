package com.gurella.engine.asset2;

import static com.gurella.engine.asset2.AssetLoadingState.asyncLoading;
import static com.gurella.engine.asset2.AssetLoadingState.finished;
import static com.gurella.engine.asset2.AssetLoadingState.ready;
import static com.gurella.engine.asset2.AssetLoadingState.syncLoading;
import static com.gurella.engine.asset2.AssetLoadingState.waitingDependencies;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.loader.DependencyCollector;
import com.gurella.engine.asset2.loader.DependencyProvider;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.utils.Values;

class AssetLoadingTask<A, T>
		implements DependencyCollector, DependencyProvider, Comparable<AssetLoadingTask<?, ?>>, Poolable {
	private static int requestSequence = Integer.MIN_VALUE;

	int priority;
	int requestId;

	final AssetId assetId = new AssetId();
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

	private AssetId propertiesId;
	private final ObjectMap<AssetId, Dependency<?>> dependencies = new ObjectMap<AssetId, Dependency<?>>();
	private final AssetId tempAssetId = new AssetId();

	void init(AssetsManager manager, FileHandle file, Class<T> assetType, AsyncCallback<T> callback, int priority) {
		this.manager = manager;
		this.file = file;
		this.assetType = assetType;
		this.callback.delegate = callback;
		this.priority = priority;

		requestId = requestSequence++;
		assetId.set(file, assetType);
		loader = resolveLoader();
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
			properties = getProperties();
			asyncData = loader.loadAsync(this, file, properties);
			state = syncLoading;
			return true;
		case syncLoading:
			asset = loader.consumeAsyncData(this, file, properties, asyncData);
			state = finished;
			return true;
		case finished:
			finish();
			return true;
		default:
			this.exception = new IllegalStateException("Invalid loading state.");
			state = finished;
			return true;
		}
	}

	private AssetLoadingState start() {
		loader.initDependencies(this, file);
		FileHandle propsHandle = Assets.getPropertiesFile(assetId.fileName, assetId.fileType, assetId.assetType);
		if (propsHandle != null) {
			addPropertiesDependency(propsHandle.path(), propsHandle.type(), AssetProperties.class);
		}
		return dependenciesResolved() ? asyncLoading : waitingDependencies;
	}

	private void finish() {
		if (exception == null) {
			callback.onSuccess(asset);
		} else {
			callback.onException(exception);
		}

		unreserveDependencies(exception != null);
	}

	void updateProgress() {
		switch (state) {
		case ready:
			notifyProgress(0);
			break;
		case waitingDependencies:
			float depProgress = getDepProgress();
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

	private float getDepProgress() {
		int size = dependencies.size;
		if (size == 0) {
			return 1;
		}

		float progres = 0;
		for (Entry<AssetId, Dependency<?>> entry : dependencies.entries()) {
			Dependency<?> dependency = entry.value;
			progres += dependency == null ? 0 : dependency.getProgress();
		}

		return Math.min(1, progres / size);
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

	void addPropertiesDependency(String fileName, FileType fileType, Class<?> assetType) {
		if (!dependencies.containsKey(tempAssetId.set(fileName, fileType, assetType))) {
			Dependency<Object> dependency = manager.reserveDependency(fileName, fileType, assetType);
			propertiesId = dependency.getAssetId();
			dependencies.put(propertiesId, dependency);
		}
	}

	<T> AssetProperties<T> getProperties() {
		return propertiesId == null ? null : this.<AssetProperties<T>> getDependency(propertiesId);
	}

	@Override
	public void addDependency(String fileName, FileType fileType, Class<?> assetType) {
		if (!dependencies.containsKey(tempAssetId.set(fileName, fileType, assetType))) {
			Dependency<Object> dependency = manager.reserveDependency(fileName, fileType, assetType);
			dependencies.put(dependency.getAssetId(), dependency);
		}
	}

	@Override
	public <D> D getDependency(String depFileName, FileType depFileType, Class<D> depAssetType) {
		return getDependency(tempAssetId.set(depFileName, depFileType, depAssetType));
	}

	private <D> D getDependency(AssetId dependencyId) {
		@SuppressWarnings("unchecked")
		Dependency<D> dependency = (Dependency<D>) dependencies.get(dependencyId);
		dependency.incDependencyCount(assetId);
		return dependency.getAsset();
	}

	boolean isEmpty() {
		return dependencies.size == 0;
	}

	private boolean dependenciesResolved() {
		int size = dependencies.size;
		if (size == 0) {
			return true;
		}

		for (Entry<AssetId, Dependency<?>> entry : dependencies.entries()) {
			Dependency<?> dependency = entry.value;
			if (dependency.getProgress() < 1) {
				return false;
			}
		}

		return true;
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
		callback.reset();
		propertiesId = null;
		dependencies.clear();
		tempAssetId.reset();
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
