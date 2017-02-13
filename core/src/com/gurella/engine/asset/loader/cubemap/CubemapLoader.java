package com.gurella.engine.asset.loader.cubemap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.CubemapData;
import com.badlogic.gdx.graphics.glutils.KTXTextureData;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class CubemapLoader implements AssetLoader<Cubemap, CubemapProperties> {
	private static final CubemapProperties defaultProperties = new CubemapProperties();

	private CubemapData cubemapData;

	@Override
	public Class<CubemapProperties> getPropertiesType() {
		return CubemapProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle file, CubemapProperties properties) {
		CubemapProperties resolved = properties == null ? defaultProperties : properties;
		cubemapData = new KTXTextureData(file, resolved.genMipMaps);
		if (!cubemapData.isPrepared()) {
			cubemapData.prepare();
		}
	}

	@Override
	public Cubemap finish(DependencySupplier provider, FileHandle file, CubemapProperties properties) {
		try {
			return createCubemap(properties);
		} finally {
			cubemapData = null;
		}
	}

	private Cubemap createCubemap(CubemapProperties properties) {
		Cubemap cubemap = new Cubemap(cubemapData);
		if (properties != null) {
			cubemap.setFilter(properties.minFilter, properties.magFilter);
			cubemap.setWrap(properties.wrapU, properties.wrapV);
		}
		return cubemap;
	}
}
