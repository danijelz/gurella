package com.gurella.engine.asset2.properties;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.Model;
import com.gurella.engine.metatype.PropertyDescriptor;

public abstract class ModelProperties implements AssetProperties<Model> {
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
