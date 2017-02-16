package com.gurella.engine.asset.loader.cubemap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.CubemapData;
import com.badlogic.gdx.graphics.glutils.KTXTextureData;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class CubemapLoader extends BaseAssetLoader<Cubemap, CubemapProperties> {
	private static final CubemapProperties defaultProperties = new CubemapProperties();

	@Override
	public Class<CubemapProperties> getPropertiesType() {
		return CubemapProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle assetFile, CubemapProperties properties) {
		CubemapProperties resolved = properties == null ? defaultProperties : properties;
		CubemapData cubemapData = new KTXTextureData(assetFile, resolved.genMipMaps);
		if (!cubemapData.isPrepared()) {
			cubemapData.prepare();
		}
		put(assetFile, cubemapData);
	}

	@Override
	public Cubemap finish(DependencySupplier provider, FileHandle assetFile, CubemapProperties properties) {
		CubemapData cubemapData = remove(assetFile);
		Cubemap cubemap = new Cubemap(cubemapData);
		if (properties != null) {
			cubemap.setFilter(properties.minFilter, properties.magFilter);
			cubemap.setWrap(properties.wrapU, properties.wrapV);
		}
		return cubemap;
	}
}
