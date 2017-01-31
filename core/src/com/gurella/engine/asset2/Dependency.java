package com.gurella.engine.asset2;

interface Dependency<T> {
	AssetId getAssetId();

	void incRefCount();

	void decRefCount(int num);

	void incDependencyCount(AssetId id);

	void decDependencyCount(AssetId id);

	float getProgress();

	T getAsset();
}
