package com.gurella.engine.asset;

import static com.gurella.engine.asset.AssetLoadingPhase.async;
import static com.gurella.engine.asset.AssetLoadingPhase.finished;
import static com.gurella.engine.asset.AssetLoadingPhase.ready;
import static com.gurella.engine.asset.AssetLoadingPhase.sync;
import static com.gurella.engine.asset.AssetLoadingPhase.waitingDependencies;

import java.util.Iterator;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
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
	final AssetLoadingTaskCallback<T> callback = new AssetLoadingTaskCallback<T>();

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
		this.callback.add(callback);
		this.priority = priority;
		this.loader = loader;

		requestId = requestSequence++;
		this.assetId.set(assetId, null, file);
	}

	void init(AssetLoadingTask<?> parent, AssetId assetId, FileHandle file, AssetLoader<T, AssetProperties> loader) {
		this.manager = parent.manager;
		this.executor = manager.executor;
		this.file = file;
		this.callback.add(parent);
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
				proceed = false;
				handleException(exception, false);
				// TODO updateProgressSafely();
			}
		}
	}

	private boolean step() {
		switch (phase) {
		case ready:
			start();
			return phase != waitingDependencies;
		case async:
			async();
			return false;
		case sync:
			sync();
			return true;
		case finished:
			finish();
			return false;
		default:
			handleException(exception, false);
			return false;
		}
	}

	private void start() {
		loader.initDependencies(this, file);
		FileHandle propsHandle = getPropertiesFile(assetId.fileName, assetId.fileType);
		if (propsHandle != null) {
			addPropertiesDependency(propsHandle.path(), propsHandle.type(), AssetProperties.class);
		}
		phase = allDependenciesResolved() ? async : waitingDependencies;
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

	private void async() {
		properties = getProperties();
		loader.processAsync(this, file, properties);
		phase = sync;
	}

	private void sync() {
		asset = loader.finish(this, file, properties);
		if (asset instanceof Bundle) {
			((Bundle) asset).getBundledAssets(bundledAssets);
		}
		phase = finished;
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

	void join(AsyncCallback<? super T> concurrentCallback, int newPriority) {
		callback.add(concurrentCallback);
		renice(newPriority);
		concurrentCallback.onProgress(progress);
	}

	private void renice(int newPriority) {
		if (priority >= newPriority) {
			return;
		}

		requestId = requestSequence++;
		priority = newPriority;

		for (Entry<AssetId, Dependency<?>> entry : dependencies.entries()) {
			Dependency<?> dependency = entry.value;
			if (dependency instanceof AssetLoadingTask) {
				((AssetLoadingTask<?>) dependency).renice(newPriority);
			}
		}
	}

	public void split(AssetLoadingTask<?> task) {
		callback.remove(task);
	}

	@Override
	public <D> void collectDependency(String fileName, FileType fileType, Class<D> assetType) {
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
	public <D> D getDependency(String depFileName, FileType depFileType, Class<?> depAssetType, String bundleId) {
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
		return callback.getReferences();
	}

	ObjectIntMap<AssetId> getDependencyCount() {
		return dependencyCount;
	}

	int getReservations() {
		return callback.getReservations();
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
		}
	}

	@Override
	public void onException(Throwable exception) {
		handleException(exception, true);
	}

	private void handleException(Throwable exception, boolean notifyManager) {
		if (phase != finished && this.exception == null) {
			this.exception = exception == null ? new RuntimeException("propagated exception is null") : exception;
			phase = finished;
			notifyDependenciesAboutException();
			if (notifyManager) {
				executor.taskStateChanged(this);
			}
		}
	}

	private void notifyDependenciesAboutException() {
		for (Entry<AssetId, Dependency<?>> entry : dependencies.entries()) {
			Dependency<?> dependency = entry.value;
			if (dependency instanceof AssetLoadingTask) {
				AssetLoadingTask<?> dependencyTask = (AssetLoadingTask<?>) dependency;
				dependencyTask.onDependantException(exception);
			}
		}
	}

	private void onDependantException(Throwable exception) {
		if (callback.callbacks.size() < 2) {
			onException(exception);
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

	boolean isActive() {
		return phase != finished || callback.isActive();
	}

	@Override
	public int compareTo(AssetLoadingTask<?> other) {
		int result = Values.compare(priority, other.priority);
		return result == 0 ? Values.compare(other.requestId, requestId) : result;
	}

	@Override
	public void reset() {
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

		return builder.toString();
	}
}
