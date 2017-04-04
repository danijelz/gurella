package com.gurella.engine.asset;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.metatype.PropertyDescriptor;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.Values;

public final class AssetReference<T> {
	@PropertyDescriptor
	private Class<T> assetType;
	@PropertyDescriptor
	private String fileName;
	@PropertyDescriptor
	private FileType fileType;

	transient T asset;

	AssetReference() {
		assetType = null;
	}

	public AssetReference(Class<T> assetType) {
		this.assetType = assetType;
	}

	public AssetReference(Class<T> assetType, String fileName, FileType fileType) {
		this.assetType = assetType;
		this.fileName = fileName;
		this.fileType = fileType;
	}

	public Class<T> getAssetType() {
		return assetType;
	}

	public String getFileName() {
		return fileName;
	}

	public FileType getFileType() {
		return fileType;
	}

	public T getAsset() {
		return asset;
	}

	public boolean isValid() {
		return assetType != null && Values.isNotBlank(fileName);
	}

	public boolean isLoaded() {
		return asset != null;
	}

	public T load() {
		if (asset == null) {
			if (!isValid()) {
				throw new RuntimeException("Invalid reference.");
			}

			asset = AssetService.load(fileName, fileType, assetType);
		}
		return asset;
	}

	public void loadAsync(AsyncCallback<T> callback) {
		if (asset != null) {
			callback.onSuccess(asset);
		} else if (isValid()) {
			@SuppressWarnings("unchecked")
			AssetReferenceCallback<T> wrapper = PoolService.obtain(AssetReferenceCallback.class);
			wrapper.parent = callback;
			wrapper.assetReference = this;
			AssetService.loadAsync(wrapper, fileName, fileType, assetType, 0);
		} else {
			callback.onException(new RuntimeException("Invalid reference."));
		}
	}

	public void unload() {
		if (asset != null) {
			AssetService.unload(asset);
		}
	}

	private static class AssetReferenceCallback<T> implements AsyncCallback<T>, Poolable {
		private AsyncCallback<T> parent;
		private AssetReference<T> assetReference;

		@Override
		public void onSuccess(T value) {
			assetReference.asset = value;
			parent.onSuccess(value);
			PoolService.free(this);
		}

		@Override
		public void onException(Throwable exception) {
			parent.onException(exception);
			PoolService.free(this);
		}

		@Override
		public void onCanceled(String message) {
			parent.onCanceled(message);
			PoolService.free(this);
		}

		@Override
		public void onProgress(float progress) {
			parent.onProgress(progress);
		}

		@Override
		public void reset() {
			parent = null;
			assetReference = null;
		}
	}
}
