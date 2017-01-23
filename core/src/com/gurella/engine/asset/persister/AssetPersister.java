package com.gurella.engine.asset.persister;

import com.badlogic.gdx.files.FileHandle;

public interface AssetPersister<T> {
	void persist(String fileName, T asset);
	
	void persist(FileHandle handle, T asset);
}
