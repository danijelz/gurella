package com.gurella.engine.asset.manager;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset.manager.AssetLoadingTask.DependencyCallback;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.utils.SynchronizedPools;

class CallbackDescriptor<T> implements Comparable<CallbackDescriptor<?>>, Poolable, AsyncCallback<T> {
	String fileName;
	Class<T> type;
	AssetLoaderParameters<T> params;

	int priority;
	AsyncCallback<T> delegate;
	FileHandle file;

	private CallbackDescriptor() {
	}

	static <T> CallbackDescriptor<T> obtain(String fileName, Class<T> assetType, AssetLoaderParameters<T> params,
			AsyncCallback<T> callback, int priority) {
		@SuppressWarnings("unchecked")
		CallbackDescriptor<T> descriptor = SynchronizedPools.obtain(CallbackDescriptor.class);
		descriptor.fileName = fileName.replaceAll("\\\\", "/");
		descriptor.type = assetType;
		descriptor.params = params;
		descriptor.priority = priority;
		descriptor.delegate = callback;
		return descriptor;
	}

	static <T> CallbackDescriptor<T> obtain(DependencyCallback<T> dependencyCallback) {
		@SuppressWarnings("unchecked")
		CallbackDescriptor<T> descriptor = SynchronizedPools.obtain(CallbackDescriptor.class);
		AssetDescriptor<T> source = dependencyCallback.descriptor;
		descriptor.fileName = source.fileName;
		descriptor.file = source.file;
		descriptor.type = source.type;
		descriptor.params = source.params;
		descriptor.priority = dependencyCallback.priority;
		descriptor.delegate = dependencyCallback;
		return descriptor;
	}

	@Override
	public int compareTo(CallbackDescriptor<?> other) {
		return Integer.compare(priority, other.priority);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(fileName);
		buffer.append(", ");
		buffer.append(type.getName());
		buffer.append(", ");
		buffer.append(priority);
		return buffer.toString();
	}

	@Override
	public void onSuccess(T value) {
		if (delegate != null) {
			delegate.onSuccess(value);
		}
	}

	@Override
	public void onException(Throwable exception) {
		if (delegate != null) {
			delegate.onException(exception);
		}
	}

	@Override
	public void onProgress(float progress) {
		if (delegate != null) {
			delegate.onProgress(progress);
		}
	}

	@Override
	public void reset() {
		priority = 0;
		delegate = null;
		fileName = null;
		type = null;
		params = null;
		file = null;
	}

	void free() {
		SynchronizedPools.free(this);
	}
}
