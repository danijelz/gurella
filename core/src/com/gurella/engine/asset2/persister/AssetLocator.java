package com.gurella.engine.asset2.persister;

import com.gurella.engine.asset2.AssetId;

public interface AssetLocator {
	AssetId getAssetId(Object asset, AssetId out);
}
