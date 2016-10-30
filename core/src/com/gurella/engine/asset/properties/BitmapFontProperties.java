package com.gurella.engine.asset.properties;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.gurella.engine.base.model.PropertyDescriptor;

public class BitmapFontProperties implements AssetProperties<BitmapFont> {
	public boolean flip = false;
	public boolean genMipMaps = false;
	@PropertyDescriptor(nullable = false)
	public TextureFilter minFilter = TextureFilter.Nearest;
	@PropertyDescriptor(nullable = false)
	public TextureFilter magFilter = TextureFilter.Nearest;
	public String atlasName = null;

	@Override
	public AssetLoaderParameters<BitmapFont> createLoaderParameters() {
		BitmapFontParameter parameter = new BitmapFontParameter();
		parameter.flip = flip;
		parameter.genMipMaps = genMipMaps;
		parameter.minFilter = minFilter == null ? TextureFilter.Nearest : minFilter;
		parameter.magFilter = magFilter == null ? TextureFilter.Nearest : magFilter;
		parameter.atlasName = atlasName;
		return parameter;
	}

	@Override
	public Class<BitmapFont> getAssetType() {
		return BitmapFont.class;
	}
}
