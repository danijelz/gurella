package com.gurella.engine.asset;

interface Dependency<T> {
	AssetId getAssetId();

	T getAsset();
	
	<B> B getAsset(String bundleId);
}
