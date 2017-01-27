package com.gurella.engine.asset2.loader;

import static com.gurella.engine.asset2.loader.AssetLoadingState.error;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.files.FileHandle;
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

	AssetLoadingTask<A, T> parent;
	AssetLoadingExecutor executor;

	FileHandle file;
	AssetLoader<A, T, AssetProperties<T>> loader;
	AssetProperties<T> props;
	AsyncCallback<T> callback;

	volatile float progress = 0;
	AssetLoadingState state = AssetLoadingState.ready;

	A asyncData;
	T asset;
	Throwable exception;

	@Override
	public int compareTo(AssetLoadingTask<?, ?> other) {
		int result = Values.compare(other.priority, priority);
		return result == 0 ? Values.compare(requestId, other.requestId) : result;
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
		}

		return null;
	}

	private void start() {
		loader.injectDependencies(dependencies, file);
		initProps();

		if (dependencies.isEmpty()) {
			loadAsync();
		} else {
			initDependencies();
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
		asyncData = loader.loadAsyncData(dependencies, file, props);
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

	private AssetLoadingTask<?, ?> findDuplicate(String otherFileName) {
		for (int i = 0; i < dependencies.size; i++) {
			AssetLoadingTask<?, ?> dependency = dependencies.get(i);
			if (dependency.fileName.equals(otherFileName)) {
				return dependency;
			}
		}
		return null;
	}

	T consumeAsyncData() {
		asset = loader.consumeAsyncData(dependencies, file, props, asyncData);
		return asset;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}
}
