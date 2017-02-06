package com.gurella.engine.asset.pack;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.asset.bundle.Bundle;

//TODO unused
public class AssetsPack implements Bundle {
	int count;
	String[] ids;
	Object[] assets;

	@Override
	public ObjectMap<String, Object> getBundledAssets(ObjectMap<String, Object> out) {
		for (int i = 0; i < count; i++) {
			out.put(ids[i], assets[i]);
		}
		return out;
	}
}
