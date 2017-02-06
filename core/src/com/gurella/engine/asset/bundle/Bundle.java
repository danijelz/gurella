package com.gurella.engine.asset.bundle;

import com.badlogic.gdx.utils.ObjectMap;

public interface Bundle {
	public ObjectMap<String, Object> getBundledAssets(ObjectMap<String, Object> out);
}
