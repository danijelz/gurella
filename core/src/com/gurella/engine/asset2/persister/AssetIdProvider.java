package com.gurella.engine.asset2.persister;

import com.gurella.engine.asset2.AssetId;

public interface AssetIdProvider {
	AssetId getAssetId(Object asset, AssetId out);
}
