package com.gurella.engine.asset2;

import static com.gurella.engine.asset2.AssetLoadingState.asyncLoading;
import static com.gurella.engine.asset2.AssetLoadingState.finished;
import static com.gurella.engine.asset2.AssetLoadingState.ready;
import static com.gurella.engine.asset2.AssetLoadingState.syncLoading;
import static com.gurella.engine.asset2.AssetLoadingState.waitingDependencies;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.loader.DependencyCollector;
import com.gurella.engine.asset2.loader.DependencyProvider;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.utils.Values;

class AssetLoadingTask<A, T> implements AsyncCallback<Object>, Dependency<T>, DependencyCollector, DependencyProvider,
		Comparable<AssetLoadingTask<?, ?>>, Poolable {
	private static int requestSequence = Integer.MIN_VALUE;

	int priority;
	int requestId;

	final AssetId assetId = new AssetId();
	final DelegatingCallback<T> callback = new DelegatingCallback<T>();

	AssetLoadingTask<?, ?> parent;

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
	private final ObjectIntMap<AssetId> dependencyCount = new ObjectIntMap<AssetId>();
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

	void init(AssetLoadingTask<?, ?> parent, FileHandle file, Class<T> assetType) {
		this.parent = parent;
		this.manager = parent.manager;
		this.file = file;
		this.assetType = assetType;
		this.callback.delegate = parent;
		this.priority = parent.priority;

		requestId = requestSequence++;
		assetId.set(file, assetType);
		loader = resolveLoader();
	}

	void update() {
		boolean proceed = false;
		while (proceed) {
			try {
				proceed = step();
			} catch (Exception exception) {
				this.exception = exception;
				state = finished;
				proceed = false;
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
			return state != waitingDependencies;
		case asyncLoading:
			properties = getProperties();
			asyncData = loader.loadAsync(this, file, properties);
			state = syncLoading;
			return false;
		case syncLoading:
			asset = loader.consumeAsyncData(this, file, properties, asyncData);
			state = finished;
			return false;
		case finished:
			finish();
			return false;
		default:
			this.exception = new IllegalStateException("Invalid loading state.");
			state = finished;
			return false;
		}
	}

	private AssetLoadingState start() {
		loader.initDependencies(this, file);
		FileHandle propsHandle = Assets.getPropertiesFile(assetId.fileName, assetId.fileType, assetId.assetType);
		if (propsHandle != null) {
			addPropertiesDependency(propsHandle.path(), propsHandle.type(), AssetProperties.class);
		}
		return allDependenciesResolved() ? asyncLoading : waitingDependencies;
	}

	private boolean allDependenciesResolved() {
		int size = dependencies.size;
		if (size == 0) {
			return true;
		}

		for (Entry<AssetId, Dependency<?>> entry : dependencies.entries()) {
			Dependency<?> dependency = entry.value;
			if (dependency instanceof AssetLoadingTask && ((AssetLoadingTask<?, ?>) dependency).state != finished) {
				return false;
			}
		}

		return true;
	}

	private void finish() {
		if (exception == null) {
			callback.onSuccess(asset);
		} else {
			callback.onException(exception);
		}
	}

	private void updateProgress() {
		float newProgress = calculateProgress();
		if (progress != newProgress) {
			progress = newProgress;
			callback.onProgress(newProgress);
		}
	}

	private float calculateProgress() {
		switch (state) {
		case ready:
			return 0;
		case waitingDependencies:
			return 0.8f * getDepProgress();
		case asyncLoading:
			return 0.8f;
		case syncLoading:
			return 0.9f;
		case finished:
			return 1;
		default:
			throw new IllegalStateException("Invalid progress state.");
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
			progres += dependency instanceof AssetLoadingTask ? ((AssetLoadingTask<?, ?>) dependency).progress : 1;
		}

		return Math.min(1, progres / size);
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

		for (Entry<AssetId, Dependency<?>> entry : dependencies.entries()) {
			Dependency<?> dependency = entry.value;
			if (dependency instanceof AssetLoadingTask) {
				((AssetLoadingTask<?, ?>) dependency).renice(newRequestId, newPriority);
			}
		}
	}

	private <D> void addPropertiesDependency(String fileName, FileType fileType, Class<D> assetType) {
		tempAssetId.set(fileName, fileType, assetType);
		@SuppressWarnings("unchecked")
		Dependency<D> dependency = (Dependency<D>) dependencies.get(tempAssetId);

		if (dependency == null) {
			dependency = manager.getDependency(this, fileName, fileType, assetType);
		}

		propertiesId = dependency.getAssetId();
		dependencies.put(propertiesId, dependency);
		dependencyCount.put(propertiesId, 1);
	}

	private <D> AssetProperties<D> getProperties() {
		return propertiesId == null ? null : this.<AssetProperties<D>> getDependency(propertiesId);
	}

	@Override
	public <D> void addDependency(String fileName, FileType fileType, Class<D> assetType) {
		tempAssetId.set(fileName, fileType, assetType);
		@SuppressWarnings("unchecked")
		Dependency<D> dependency = (Dependency<D>) dependencies.get(tempAssetId);

		if (dependency == null) {
			dependency = manager.getDependency(this, fileName, fileType, assetType);
			AssetId dependencyId = dependency.getAssetId();
			dependencies.put(dependencyId, dependency);
			dependencyCount.put(dependencyId, 1);
		} else {
			dependencyCount.getAndIncrement(dependency.getAssetId(), 0, 1);
		}

	}

	@Override
	public <D> D getDependency(String depFileName, FileType depFileType, Class<D> depAssetType) {
		return getDependency(tempAssetId.set(depFileName, depFileType, depAssetType));
	}

	private <D> D getDependency(AssetId dependencyId) {
		@SuppressWarnings("unchecked")
		Dependency<D> dependency = (Dependency<D>) dependencies.get(dependencyId);
		return dependency.getAsset();
	}

	Entries<AssetId, Dependency<?>> getDependencies() {
		return dependencies.entries();
	}

	boolean isSticky() {
		// TODO Auto-generated method stub
		return properties == null ? false : false;
	}

	int getReferences() {
		return 1 + callback.concurrentCallbacks.size;
	}

	ObjectIntMap<AssetId> getDependencyCount() {
		return dependencyCount;
	}

	int getReservations() {
		return parent == null ? 0 : 1;
	}

	boolean isRoot() {
		return parent == null;
	}

	@Override
	public AssetId getAssetId() {
		return assetId;
	}

	@Override
	public T getAsset() {
		return asset;
	}

	@Override
	public void onSuccess(Object value) {
		updateProgress();
		if (state == waitingDependencies && allDependenciesResolved()) {
			state = asyncLoading;
			updateProgress();
			manager.taskStateChanged(this);
		}
	}

	@Override
	public void onException(Throwable exception) {
		if (this.exception == null) {
			this.exception = exception == null ? new RuntimeException("propagated exception is null") : exception;
			state = finished;
			updateProgress();
			manager.taskStateChanged(this);
		}
	}

	@Override
	public void onCanceled(String message) {
		onException(new RuntimeException("Loading cancled: " + message));
	}

	@Override
	public void onProgress(float progress) {
		updateProgress();
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
		dependencyCount.clear();
		tempAssetId.reset();
	}

	private static class DelegatingCallback<T> implements AsyncCallback<T> {
		private AsyncCallback<? super T> delegate;
		private final Array<AsyncCallback<? super T>> concurrentCallbacks = new Array<AsyncCallback<? super T>>();

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
		public void onCanceled(String message) {
			delegate.onCanceled(message);
			for (int i = 0, n = concurrentCallbacks.size; i < n; i++) {
				concurrentCallbacks.get(i).onCanceled(message);
			}
		}

		@Override
		public void onProgress(float progress) {
			delegate.onProgress(progress);
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
