package com.gurella.engine.serialization.json;

public interface AssetProvider {
	<T> T getAsset(String fileName);
}
