package com.gurella.engine.asset.properties;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

public class ModelProperties extends TextureProperties {
	public ModelProperties() {
		minFilter = magFilter = TextureFilter.Linear;
		wrapU = wrapV = TextureWrap.Repeat;
	}
}
