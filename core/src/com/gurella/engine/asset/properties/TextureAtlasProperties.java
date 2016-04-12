package com.gurella.engine.asset.properties;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader.TextureAtlasParameter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TextureAtlasProperties implements AssetProperties<TextureAtlas> {
	public boolean flip;

	@Override
	public AssetLoaderParameters<TextureAtlas> createLoaderParameters() {
		TextureAtlasParameter textureAtlasParameter = new TextureAtlasParameter();
		textureAtlasParameter.flip = flip;
		return textureAtlasParameter;
	}
}
