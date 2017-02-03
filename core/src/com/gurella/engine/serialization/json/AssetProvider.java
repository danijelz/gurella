package com.gurella.engine.serialization.json;

//TODO remove
public interface AssetProvider {
	<T> T getAsset(String fileName);
}
