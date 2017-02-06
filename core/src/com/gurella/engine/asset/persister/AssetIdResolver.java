package com.gurella.engine.asset.persister;

import com.gurella.engine.asset.AssetId;

//TODO rename (AssetIdRepository)
public interface AssetIdResolver {
	AssetId getAssetId(Object asset, AssetId out);
}
