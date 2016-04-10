package com.gurella.engine.asset.properties;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.gurella.engine.base.model.PropertyDescriptor;

public class TextureProperties implements AssetProperties<Texture> {
	@PropertyDescriptor(nullable = false)
	public TextureFilter minFilter = TextureFilter.Nearest;
	@PropertyDescriptor(nullable = false)
	public TextureFilter magFilter = TextureFilter.Nearest;
	@PropertyDescriptor(nullable = false)
	public TextureWrap wrapU = TextureWrap.ClampToEdge;
	@PropertyDescriptor(nullable = false)
	public TextureWrap wrapV = TextureWrap.ClampToEdge;
	public boolean genMipMaps = false;

	@Override
	public AssetLoaderParameters<Texture> createLoaderParameters() {
		TextureParameter textureParameter = new TextureParameter();
		textureParameter.genMipMaps = false;
		textureParameter.minFilter = TextureFilter.Nearest;
		textureParameter.magFilter = TextureFilter.Nearest;
		textureParameter.wrapU = TextureWrap.ClampToEdge;
		textureParameter.wrapV = TextureWrap.ClampToEdge;
		return textureParameter;
	}
}
