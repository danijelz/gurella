package com.gurella.engine.asset.persister;

public interface AssetPersister<T> {
	void persist(String fileName, T asset);
}
