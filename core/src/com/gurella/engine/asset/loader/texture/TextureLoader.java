package com.gurella.engine.asset.loader.texture;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class TextureLoader implements AssetLoader<TextureData, Texture, TextureProperties> {
	private static final TextureProperties defaultProperties = new TextureProperties();

	@Override
	public Class<TextureProperties> getPropertiesType() {
		return TextureProperties.class;
	}

	@Override
	public TextureData init(DependencyCollector collector, FileHandle assetFile) {
		return null;
	}

	@Override
	public TextureData processAsync(DependencySupplier provider, FileHandle file, TextureData asyncData,
			TextureProperties properties) {
		TextureProperties resolved = properties == null ? defaultProperties : properties;
		TextureData textureData = TextureData.Factory.loadFromFile(file, resolved.format, resolved.generateMipMaps);
		if (!textureData.isPrepared()) {
			textureData.prepare();
		}
		return textureData;
	}

	@Override
	public Texture finish(DependencySupplier provider, FileHandle file, TextureData asyncData,
			TextureProperties properties) {
		Texture texture = new Texture(asyncData);
		if (properties != null) {
			texture.setFilter(properties.minFilter, properties.magFilter);
			texture.setWrap(properties.wrapU, properties.wrapV);
		}
		return texture;
	}
}
