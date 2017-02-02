package com.gurella.engine.asset2.persister;

import com.badlogic.gdx.files.FileHandle;

public interface AssetPersister<T> {
	void persist(AssetIdProvider assetIdProvider, FileHandle file, T asset);
}
