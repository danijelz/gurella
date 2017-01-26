package com.gurella.engine.asset2.persister;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.registry.AssetRegistry;

public interface AssetPersister<T> {
	void persist(AssetRegistry registry, FileHandle file, T asset);
}
