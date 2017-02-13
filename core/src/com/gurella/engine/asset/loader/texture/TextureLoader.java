package com.gurella.engine.asset.loader.texture;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class TextureLoader implements AssetLoader<Texture, TextureProperties> {
	private static final TextureProperties defaultProperties = new TextureProperties();

	private TextureData textureData;

	@Override
	public Class<TextureProperties> getPropertiesType() {
		return TextureProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle file, TextureProperties properties) {
		TextureProperties resolved = properties == null ? defaultProperties : properties;
		textureData = TextureData.Factory.loadFromFile(file, resolved.format, resolved.generateMipMaps);
		if (!textureData.isPrepared()) {
			textureData.prepare();
		}
	}

	@Override
	public Texture finish(DependencySupplier provider, FileHandle file, TextureProperties properties) {
		try {
			return createTexture(properties);
		} finally {
			textureData = null;
		}
	}

	private Texture createTexture(TextureProperties properties) {
		Texture texture = new Texture(textureData);
		if (properties != null) {
			texture.setFilter(properties.minFilter, properties.magFilter);
			texture.setWrap(properties.wrapU, properties.wrapV);
		}
		return texture;
	}
}
