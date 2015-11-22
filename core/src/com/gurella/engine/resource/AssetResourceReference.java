package com.gurella.engine.resource;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.resource.factory.AssetResourceFactory;

public class AssetResourceReference<T> extends SharedResourceReference<T> {
	protected AssetResourceReference() {
	}

	public AssetResourceReference(int id, boolean persistent, boolean initOnStart, AssetDescriptor<T> assetDescriptor) {
		super(id, assetDescriptor.fileName, persistent, initOnStart, new AssetResourceFactory<T>(assetDescriptor));
	}

	public AssetResourceReference(int id, boolean persistent, boolean initOnStart, String fileName, Class<T> type) {
		super(id, fileName, persistent, initOnStart, new AssetResourceFactory<T>(fileName, type));
	}

	public AssetResourceReference(int id, boolean persistent, boolean initOnStart, FileHandle file, Class<T> type) {
		super(id, file.path(), persistent, initOnStart, new AssetResourceFactory<T>(file, type));
	}

	public AssetResourceReference(int id, String name, boolean persistent, boolean initOnStart,
			AssetDescriptor<T> assetDescriptor) {
		super(id, name, persistent, initOnStart, new AssetResourceFactory<T>(assetDescriptor));
	}

	public AssetResourceReference(int id, AssetResourceDescriptor<T> assetResourceDescriptor) {
		super(id, assetResourceDescriptor.fileName, assetResourceDescriptor.persistent,
				assetResourceDescriptor.initOnStart, new AssetResourceFactory<T>(assetResourceDescriptor));
	}

	@Override
	public AssetResourceFactory<T> getResourceFactory() {
		return (AssetResourceFactory<T>) super.getResourceFactory();
	}

	@Override
	void disposeResource() {
		getResourceFactory().unload();
	}
}
