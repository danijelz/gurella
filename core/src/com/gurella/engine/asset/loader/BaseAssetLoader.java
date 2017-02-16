package com.gurella.engine.asset.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;

public abstract class BaseAssetLoader<TYPE, PROPS extends AssetProperties> implements AssetLoader<TYPE, PROPS> {
	private final ObjectMap<FileHandle, Object> asyncValues = new ObjectMap<FileHandle, Object>();

	protected void put(FileHandle file, Object value) {
		asyncValues.put(file, value);
	}

	protected <T> T get(FileHandle file) {
		@SuppressWarnings("unchecked")
		T result = (T) asyncValues.get(file);
		return result;
	}

	protected <T> T remove(FileHandle file) {
		@SuppressWarnings("unchecked")
		T result = (T) asyncValues.remove(file);
		return result;
	}

	protected void clear(FileHandle file) {
		asyncValues.remove(file);
	}
}
