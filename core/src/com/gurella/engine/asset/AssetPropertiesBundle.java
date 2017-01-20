package com.gurella.engine.asset;

import com.badlogic.gdx.utils.IdentityMap;
import com.gurella.engine.asset.properties.AssetProperties;

//TODO unused
public class AssetPropertiesBundle<T> implements Bundle {
	private T asset;
	private AssetProperties<T> properties;
	
	@Override
	public IdentityMap<String, Object> getBundledAssets() {
		IdentityMap<String, Object> bundledAssets = new IdentityMap<String, Object>();
		bundledAssets.put("", asset);
		bundledAssets.put("", asset);
		// TODO Auto-generated method stub
		return null;
	}
}
