package com.gurella.engine.asset.loader.polygonregion;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class PolygonRegionLoader implements AssetLoader<PolygonRegion, AssetProperties> {
	@Override
	public Class<AssetProperties> getPropertiesType() {
		return null;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		// TODO Auto-generated method stub
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle file, AssetProperties properties) {
		// TODO Auto-generated method stub
	}

	@Override
	public PolygonRegion finish(DependencySupplier provider, FileHandle file, AssetProperties properties) {
		// TODO Auto-generated method stub
		return null;
	}
}
