package com.gurella.engine.asset2.persister;

import com.gurella.engine.asset2.AssetId;

//TODO rename (AssetIdRepository)
public interface AssetIdResolver {
	AssetId getAssetId(Object asset, AssetId out);
}
