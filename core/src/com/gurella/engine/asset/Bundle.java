package com.gurella.engine.asset;

import com.badlogic.gdx.utils.IdentityMap;

public interface Bundle {
	public IdentityMap<String, Object> getBundledAssets();
}
