package com.gurella.engine.asset2;

import com.badlogic.gdx.utils.Pool;

public final class AssetIdPool extends Pool<AssetId> {
	@Override
	protected AssetId newObject() {
		return new AssetId();
	}
}