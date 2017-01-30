package com.gurella.engine.asset2.persister;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.AssetsManager;

public interface AssetPersister<T> {
	void persist(AssetsManager manager, FileHandle file, T asset);
}
