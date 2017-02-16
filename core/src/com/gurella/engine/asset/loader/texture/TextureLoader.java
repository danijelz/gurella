package com.gurella.engine.asset.loader.texture;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class TextureLoader extends BaseAssetLoader<Texture, TextureProperties> {
	private static final TextureProperties defaultProperties = new TextureProperties();

	@Override
	public Class<TextureProperties> getPropertiesType() {
		return TextureProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle assetFile, TextureProperties properties) {
		TextureProperties resolved = properties == null ? defaultProperties : properties;
		TextureData textureData = TextureData.Factory.loadFromFile(assetFile, resolved.format,
				resolved.generateMipMaps);
		if (!textureData.isPrepared()) {
			textureData.prepare();
		}
		put(assetFile, textureData);
	}

	@Override
	public Texture finish(DependencySupplier provider, FileHandle assetFile, TextureProperties properties) {
		TextureData textureData = remove(assetFile);
		Texture texture = new Texture(textureData);
		if (properties != null) {
			texture.setFilter(properties.minFilter, properties.magFilter);
			texture.setWrap(properties.wrapU, properties.wrapV);
		}
		return texture;
	}
}
