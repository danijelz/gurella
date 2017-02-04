package com.gurella.engine.asset2.loader.cubemap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.CubemapData;
import com.badlogic.gdx.graphics.glutils.KTXTextureData;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.loader.DependencyCollector;
import com.gurella.engine.asset2.loader.DependencyProvider;

public class CubemapLoader implements AssetLoader<CubemapData, Cubemap, CubemapProperties> {
	private static final CubemapProperties defaultProperties = new CubemapProperties();

	@Override
	public Class<CubemapProperties> getAssetPropertiesType() {
		return CubemapProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public CubemapData loadAsyncData(DependencyProvider provider, FileHandle file, CubemapProperties properties) {
		CubemapProperties resolved = properties == null ? defaultProperties : properties;
		CubemapData data = new KTXTextureData(file, resolved.genMipMaps);
		if (!data.isPrepared()) {
			data.prepare();
		}
		return data;
	}

	@Override
	public Cubemap consumeAsyncData(DependencyProvider provider, FileHandle file, CubemapProperties properties,
			CubemapData asyncData) {
		Cubemap cubemap = new Cubemap(asyncData);
		if (properties != null) {
			cubemap.setFilter(properties.minFilter, properties.magFilter);
			cubemap.setWrap(properties.wrapU, properties.wrapV);
		}
		return cubemap;
	}
}
