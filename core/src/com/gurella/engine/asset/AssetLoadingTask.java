package com.gurella.engine.asset;

import static com.gurella.engine.asset.AssetLoadingPhase.async;
import static com.gurella.engine.asset.AssetLoadingPhase.finished;
import static com.gurella.engine.asset.AssetLoadingPhase.ready;
import static com.gurella.engine.asset.AssetLoadingPhase.sync;
import static com.gurella.engine.asset.AssetLoadingPhase.waitingDependencies;

import java.util.Iterator;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.bundle.Bundle;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.utils.Values;

class AssetLoadingTask<T> implements AsyncCallback<Object>, Dependency<T>, DependencyCollector, DependencySupplier,
		Comparable<AssetLoadingTask<?>>, Poolable {
	private static int requestSequence = Integer.MIN_VALUE;

	int priority;
	int requestId;

	final AssetId assetId = new AssetId();
	final DelegatingCallback<T> callback = new DelegatingCallback<T>();

	AssetLoadingTask<?> parent;

	AssetsManager manager;
	AssetLoadingExecutor executor;
	FileHandle file;

	AssetLoader<T, AssetProperties> loader;
	AssetProperties properties;

	volatile AssetLoadingPhase phase = ready;
	volatile float progress = 0;

	T asset;
	final ObjectMap<String, Object> bundledAssets = new ObjectMap<String, Object>();
	Throwable exception;

	private AssetId propertiesId;
	private final ObjectMap<AssetId, Dependency<?>> dependencies = new ObjectMap<AssetId, Dependency<?>>();
	private final ObjectIntMap<AssetId> dependencyCount = new ObjectIntMap<AssetId>();
	private final AssetId tempAssetId = new AssetId();

	void init(AssetsManager manager, AssetId assetId, FileHandle file, AssetLoader<T, AssetProperties> loader,
			AsyncCallback<? super T> callback, int priority) {
		this.manager = manager;
		this.executor = manager.executor;
		this.file = file;
		this.callback.delegate = callback;
		this.priority = priority;
		this.loader = loader;

		requestId = requestSequence++;
		this.assetId.set(assetId, file);
	}

	void init(AssetLoadingTask<?> parent, AssetId assetId, FileHandle file, AssetLoader<T, AssetProperties> loader) {
		this.parent = parent;
		this.manager = parent.manager;
		this.executor = manager.executor;
		this.file = file;
		this.callback.delegate = parent;
		this.priority = parent.priority;
		this.loader = loader;

		requestId = requestSequence++;
		this.assetId.set(assetId, file);
	}

	void update() {
		boolean proceed = true;
		while (proceed) {
			try {
				proceed = step();
				updateProgress();
			} catch (Exception exception) {
				this.exception = exception;
				phase = finished;
				proceed = false;
				// TODO updateProgressSafely();
			}
		}
	}

	private boolean step() {
		switch (phase) {
		case ready:
			phase = start();
			return phase != waitingDependencies;
		case async:
			properties = getProperties();
			loader.processAsync(this, file, properties);
			phase = sync;
			return false;
		case sync:
			asset = loader.finish(this, file, properties);
			if (asset instanceof Bundle) {
				((Bundle) asset).getBundledAssets(bundledAssets);
			}
			phase = finished;
			return true;
		case finished:
			finish();
			return false;
		default:
			this.exception = new IllegalStateException("Invalid loading state.");
			phase = finished;
			return false;
		}
	}

	private AssetLoadingPhase start() {
		loader.initDependencies(this, file);
		FileHandle propsHandle = getPropertiesFile(assetId.fileName, assetId.fileType);
		if (propsHandle != null) {
			addPropertiesDependency(propsHandle.path(), propsHandle.type(), AssetProperties.class);
		}
		return allDependenciesResolved() ? async : waitingDependencies;
	}

	private FileHandle getPropertiesFile(String assetFileName, FileType fileType) {
		if (loader.getPropertiesType() == null) {
			return null;
		}

		String propertiesFileName = Assets.toPropertiesFileName(assetFileName);
		FileHandle propsHandle = manager.getFileHandle(propertiesFileName, fileType);
		return propsHandle.exists() ? propsHandle : null;
	}

	private boolean allDependenciesResolved() {
		if (dependencies.size == 0) {
			return true;
		}

		for (Entry<AssetId, Dependency<?>> entry : dependencies.entries()) {
			Dependency<?> dependency = entry.value;
			if (dependency instanceof AssetLoadingTask && ((AssetLoadingTask<?>) dependency).phase != finished) {
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
		switch (phase) {
		case ready:
			return 0;
		case waitingDependencies:
			return 0.8f * getDepProgress();
		case async:
			return 0.8f;
		case sync:
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
			progres += dependency instanceof AssetLoadingTask ? ((AssetLoadingTask<?>) dependency).progress : 1;
		}

		return Math.min(1, progres / size);
	}

	void merge(AsyncCallback<? super T> concurrentCallback, int newPriority) {
		callback.concurrentCallbacks.add(concurrentCallback);
		if (priority < newPriority) {
			renice(requestSequence++, newPriority);
		}
		concurrentCallback.onProgress(progress);
	}
	
	void merge(AssetLoadingTask<?> parent) {
		this.parent = parent;
		callback.concurrentCallbacks.add(callback.delegate);
		callback.delegate = parent;
		int newPriority = parent.priority;
		if (priority < newPriority) {
			renice(requestSequence++, newPriority);
		}
		parent.onProgress(progress);
	}

	private void renice(int newRequestId, int newPriority) {
		requestId = newRequestId;
		priority = newPriority;

		for (Entry<AssetId, Dependency<?>> entry : dependencies.entries()) {
			Dependency<?> dependency = entry.value;
			if (dependency instanceof AssetLoadingTask) {
				((AssetLoadingTask<?>) dependency).renice(newRequestId, newPriority);
			}
		}
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
		}
	}

	@Override
	public <D> D getDependency(String depFileName, FileType depFileType, Class<D> depAssetType, String bundleId) {
		tempAssetId.set(depFileName, depFileType, depAssetType);
		@SuppressWarnings("unchecked")
		Dependency<D> dependency = (Dependency<D>) dependencies.get(tempAssetId);
		dependencyCount.getAndIncrement(dependency.getAssetId(), 0, 1);
		return bundleId == null ? dependency.getAsset() : dependency.<D> getAsset(bundleId);
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
	}

	private AssetProperties getProperties() {
		if (propertiesId == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		Dependency<AssetProperties> dependency = (Dependency<AssetProperties>) dependencies.get(propertiesId);
		AssetProperties properties = dependency.getAsset();
		if (ClassReflection.isInstance(loader.getPropertiesType(), properties)) {
			dependencyCount.getAndIncrement(propertiesId, 0, 1);
			return properties;
		} else {
			return null;
		}
	}

	Entries<AssetId, Dependency<?>> getDependencies() {
		return dependencies.entries();
	}

	boolean isSticky() {
		return properties == null ? false : properties.sticky;
	}

	int getReferences() {
		return (parent == null ? 1 : 0) + callback.concurrentCallbacks.size;
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
	public <B> B getAsset(String bundleId) {
		if (!(asset instanceof Bundle)) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("unchecked")
		B casted = (B) bundledAssets.get(bundleId);
		return casted;
	}

	@Override
	public void onSuccess(Object value) {
		if (phase == waitingDependencies && allDependenciesResolved()) {
			phase = async;
			executor.taskStateChanged(this);
			updateProgress();
		} else {
			updateProgress();
		}
	}

	@Override
	public void onException(Throwable exception) {
		if (phase != finished && this.exception == null) {
			this.exception = exception == null ? new RuntimeException("propagated exception is null") : exception;
			phase = finished;
			notifyDependenciesAboutException();
			executor.taskStateChanged(this);
			updateProgress();
		}
	}

	private void onParentException(Throwable exception) {
		if (callback.concurrentCallbacks.size == 0) {
			onException(exception);
		}
	}

	private void notifyDependenciesAboutException() {
		for (Entry<AssetId, Dependency<?>> entry : dependencies.entries()) {
			Dependency<?> dependency = entry.value;
			if (dependency instanceof AssetLoadingTask) {
				AssetLoadingTask<?> dependencyTask = (AssetLoadingTask<?>) dependency;
				dependencyTask.onParentException(exception);
			}
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
	public int compareTo(AssetLoadingTask<?> other) {
		int result = Values.compare(priority, other.priority);
		return result == 0 ? Values.compare(requestId, other.requestId) : result;
	}

	@Override
	public void reset() {
		parent = null;
		manager = null;
		executor = null;
		file = null;
		loader = null;
		properties = null;
		asset = null;
		bundledAssets.clear();
		exception = null;
		priority = 0;
		requestId = 0;
		phase = ready;
		progress = 0;
		assetId.reset();
		callback.reset();
		propertiesId = null;
		dependencies.clear();
		dependencyCount.clear();
		tempAssetId.reset();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(assetId.fileName);
		builder.append(", ");
		builder.append(assetId.assetType.getSimpleName());
		builder.append(" phase: ");
		builder.append(phase.name());
		builder.append(", refCount: ");
		builder.append(getReferences());

		if (dependencies.size > 0) {
			builder.append(", dependencies: [");
			ObjectIntMap.Entries<AssetId> entries = dependencyCount.entries();
			for (Iterator<ObjectIntMap.Entry<AssetId>> iter = entries; iter.hasNext();) {
				ObjectIntMap.Entry<AssetId> entry = iter.next();
				AssetId dependencyId = entry.key;
				builder.append(dependencyId.fileName + " (" + entry.value + ")");
				if (iter.hasNext()) {
					builder.append(", ");
				}
			}

			builder.append("]");
		}

		builder.append("\n");
		return builder.toString();
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
