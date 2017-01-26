package com.gurella.engine.asset.persister;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.AssetRegistry;

public interface AssetPersister<T> {
	void persist(AssetRegistry registry, FileHandle file, T asset);
}
