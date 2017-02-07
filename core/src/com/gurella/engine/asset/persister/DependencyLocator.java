package com.gurella.engine.asset.persister;

import com.gurella.engine.asset.AssetId;

public interface DependencyLocator {
	AssetId getAssetId(Object asset, AssetId out);
}
