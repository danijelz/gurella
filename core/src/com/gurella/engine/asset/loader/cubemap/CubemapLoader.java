package com.gurella.engine.asset.loader.cubemap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.CubemapData;
import com.badlogic.gdx.graphics.glutils.KTXTextureData;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencyProvider;

public class CubemapLoader implements AssetLoader<CubemapData, Cubemap, CubemapProperties> {
	private static final CubemapProperties defaultProperties = new CubemapProperties();

	@Override
	public Class<CubemapProperties> getAssetPropertiesType() {
		return CubemapProperties.class;
	}

	@Override
	public CubemapData init(DependencyCollector collector, FileHandle assetFile) {
		return null;
	}

	@Override
	public CubemapData processAsync(DependencyProvider provider, FileHandle file, CubemapData asyncData,
			CubemapProperties properties) {
		CubemapProperties resolved = properties == null ? defaultProperties : properties;
		CubemapData data = new KTXTextureData(file, resolved.genMipMaps);
		if (!data.isPrepared()) {
			data.prepare();
		}
		return data;
	}

	@Override
	public Cubemap finish(DependencyProvider provider, FileHandle file, CubemapData asyncData,
			CubemapProperties properties) {
		Cubemap cubemap = new Cubemap(asyncData);
		if (properties != null) {
			cubemap.setFilter(properties.minFilter, properties.magFilter);
			cubemap.setWrap(properties.wrapU, properties.wrapV);
		}
		return cubemap;
	}
}
