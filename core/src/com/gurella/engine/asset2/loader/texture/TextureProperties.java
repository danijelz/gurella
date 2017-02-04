package com.gurella.engine.asset2.loader.texture;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.gurella.engine.asset2.loader.AssetProperties;
import com.gurella.engine.metatype.PropertyDescriptor;

public class TextureProperties extends AssetProperties {
	@PropertyDescriptor(nullable = false)
	public TextureFilter minFilter = TextureFilter.Nearest;
	@PropertyDescriptor(nullable = false)
	public TextureFilter magFilter = TextureFilter.Nearest;
	@PropertyDescriptor(nullable = false)
	public TextureWrap wrapU = TextureWrap.ClampToEdge;
	@PropertyDescriptor(nullable = false)
	public TextureWrap wrapV = TextureWrap.ClampToEdge;
	public Format format = null;
	public boolean genMipMaps = false;
}
