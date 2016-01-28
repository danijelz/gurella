package com.gurella.engine.asset.manager;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.base.resource.AsyncCallback;

class CallbackAssetDescriptor<T> implements Comparable<CallbackAssetDescriptor<?>> {
	public int priority;
	public AsyncCallback<T> callback;

	public final String fileName;
	public final Class<T> type;
	public final AssetLoaderParameters<T> params;
	/** The resolved file. May be null if the fileName has not been resolved yet. */
	public FileHandle file;

	public CallbackAssetDescriptor(String fileName, Class<T> assetType, AsyncCallback<T> callback, int priority) {
		this(fileName, assetType, null, callback, priority);
	}

	/** Creates an AssetDescriptor with an already resolved name. */
	public CallbackAssetDescriptor(FileHandle file, Class<T> assetType, AsyncCallback<T> callback, int priority) {
		this(file, assetType, null, callback, priority);
	}

	public CallbackAssetDescriptor(String fileName, Class<T> assetType, AssetLoaderParameters<T> params,
			AsyncCallback<T> callback, int priority) {
		this.fileName = fileName.replaceAll("\\\\", "/");
		this.type = assetType;
		this.params = params;
		this.priority = priority;
		this.callback = callback;
	}

	/** Creates an AssetDescriptor with an already resolved name. */
	public CallbackAssetDescriptor(FileHandle file, Class<T> assetType, AssetLoaderParameters<T> params,
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
}
