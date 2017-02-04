package com.gurella.engine.asset.properties;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.gurella.engine.metatype.PropertyDescriptor;

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
		textureParameter.genMipMaps = genMipMaps;
		textureParameter.minFilter = minFilter == null ? TextureFilter.Nearest : minFilter;
		textureParameter.magFilter = magFilter == null ? TextureFilter.Nearest : magFilter;
		textureParameter.wrapU = wrapU == null ? TextureWrap.ClampToEdge : wrapU;
		textureParameter.wrapV = wrapV == null ? TextureWrap.ClampToEdge : wrapV;
		return textureParameter;
	}
}
