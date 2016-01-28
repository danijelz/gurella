package com.gurella.engine.asset.manager;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.utils.SynchronizedPools;

class CallbackAssetDescriptor<T> implements Comparable<CallbackAssetDescriptor<?>>, Poolable {
	public int priority;
	public AsyncCallback<T> callback;

	public String fileName;
	public Class<T> type;
	public AssetLoaderParameters<T> params;

	/** The resolved file. May be null if the fileName has not been resolved yet. */
	public FileHandle file;

	static <T> CallbackAssetDescriptor<T> obtain(String fileName, Class<T> assetType, AssetLoaderParameters<T> params,
			AsyncCallback<T> callback, int priority) {
		@SuppressWarnings("unchecked")
		CallbackAssetDescriptor<T> descriptor = SynchronizedPools.obtain(CallbackAssetDescriptor.class);
		descriptor.fileName = fileName.replaceAll("\\\\", "/");
		descriptor.type = assetType;
		descriptor.params = params;
		descriptor.priority = priority;
		descriptor.callback = callback;
		return descriptor;
	}

	private CallbackAssetDescriptor() {
	}

	/** Creates an AssetDescriptor with an already resolved name. */
	private CallbackAssetDescriptor(FileHandle file, Class<T> assetType, AssetLoaderParameters<T> params,
			AsyncCallback<T> callback, int priority) {
		this.fileName = file.path().replaceAll("\\\\", "/");
		this.file = file;
		this.type = assetType;
		this.params = params;
		this.priority = priority;
		this.callback = callback;
	}

	@Override
	public int compareTo(CallbackAssetDescriptor<?> other) {
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
	public void reset() {
		priority = 0;
		callback = null;
		fileName = null;
		type = null;
		params = null;
		file = null;
	}

	public void free() {
		SynchronizedPools.free(this);
	}
}
