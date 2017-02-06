package com.gurella.engine.asset.loader.texture;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.metatype.PropertyDescriptor;

public class TextureProperties extends AssetProperties {
	@PropertyDescriptor(nullable = false)
	public final TextureFilter minFilter = TextureFilter.Nearest;
	@PropertyDescriptor(nullable = false)
	public final TextureFilter magFilter = TextureFilter.Nearest;
	@PropertyDescriptor(nullable = false)
	public final TextureWrap wrapU = TextureWrap.ClampToEdge;
	@PropertyDescriptor(nullable = false)
	public final TextureWrap wrapV = TextureWrap.ClampToEdge;
	@PropertyDescriptor()
	public final Format format = null;
	@PropertyDescriptor()
	public final boolean generateMipMaps = false;
}
