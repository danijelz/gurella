package com.gurella.engine.asset.loader.model;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.metatype.PropertyDescriptor;

public abstract class ModelProperties extends AssetProperties {
	@PropertyDescriptor(nullable = false)
	public TextureFilter minFilter = TextureFilter.Nearest;
	@PropertyDescriptor(nullable = false)
	public TextureFilter magFilter = TextureFilter.Nearest;
	@PropertyDescriptor(nullable = false)
	public TextureWrap wrapU = TextureWrap.ClampToEdge;
	@PropertyDescriptor(nullable = false)
	public TextureWrap wrapV = TextureWrap.ClampToEdge;
	public boolean genMipMaps = false;
}
