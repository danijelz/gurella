package com.gurella.engine.asset.persister;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public abstract class AbstractAssetPersister<T> implements AssetPersister<T> {
	private FileHandleResolver resolver;

	public AbstractAssetPersister(FileHandleResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void persist(String fileName, T asset) {
		persist(resolve(fileName), asset);
	}

	protected abstract void persist(FileHandle file, T asset);

	public FileHandle resolve(String fileName) {
		return resolver.resolve(fileName);
	}
}
