package com.gurella.engine.asset.persister;

import com.badlogic.gdx.files.FileHandle;

public interface AssetPersister<T> {
	void persist(DependencyLocator dependencyLocator, FileHandle file, T asset);
}
