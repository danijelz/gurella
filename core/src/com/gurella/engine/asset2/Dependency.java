package com.gurella.engine.asset2;

//TODO unused
interface Dependency {
	void incRefCount();

	void decRefCount();

	void incDependencyCount(AssetId id);

	void decDependencyCount(AssetId id);
}
