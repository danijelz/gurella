package com.gurella.engine.asset2.loader.texture;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.loader.DependencyCollector;
import com.gurella.engine.asset2.loader.DependencyProvider;

public class TextureLoader implements AssetLoader<TextureData, Texture, TextureProperties> {
	private static final TextureProperties defaultProperties = new TextureProperties();

	@Override
	public Class<TextureProperties> getAssetPropertiesType() {
		return TextureProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public TextureData loadAsyncData(DependencyProvider provider, FileHandle file, TextureProperties properties) {
		TextureProperties resolved = properties == null ? defaultProperties : properties;
		TextureData textureData = TextureData.Factory.loadFromFile(file, resolved.format, resolved.genMipMaps);
		if (!textureData.isPrepared()) {
			textureData.prepare();
		}
		return textureData;
	}

	@Override
	public Texture consumeAsyncData(DependencyProvider provider, FileHandle file, TextureProperties properties,
			TextureData asyncData) {
		Texture texture = new Texture(asyncData);
		if (properties != null) {
			texture.setFilter(properties.minFilter, properties.magFilter);
			texture.setWrap(properties.wrapU, properties.wrapV);
		}
		return texture;
	}
}
