package com.gurella.engine.asset;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.asset.properties.AssetProperties;
import com.gurella.engine.asset2.bundle.Bundle;

//TODO unused
public class AssetPropertiesBundle<T> implements Bundle {
	private T asset;
	private AssetProperties<T> properties;

	@Override
	public ObjectMap<String, Object> getBundledAssets(ObjectMap<String, Object> out) {
		ObjectMap<String, Object> bundledAssets = new ObjectMap<String, Object>();
		bundledAssets.put("", asset);
		bundledAssets.put("", asset);
		// TODO Auto-generated method stub
		return null;
	}
}
