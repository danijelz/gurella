package com.gurella.engine.asset.properties;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.PixmapLoader.PixmapParameter;
import com.badlogic.gdx.graphics.Pixmap;

public class PixmapProperties implements AssetProperties<Pixmap> {
	@Override
	public AssetLoaderParameters<Pixmap> createLoaderParameters() {
		return new PixmapParameter();
	}
}
