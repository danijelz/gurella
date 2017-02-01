package com.gurella.engine.asset2;

interface Dependency<T> {
	AssetId getAssetId();

	T getAsset();
}
