package com.gurella.engine.asset2.loader.bitmapfont;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.loader.DependencyCollector;
import com.gurella.engine.asset2.loader.DependencyProvider;

public class BitmapFontLoader implements AssetLoader<BitmapFontData, BitmapFont, BitmapFontProperties>{
	@Override
	public Class<BitmapFontProperties> getAssetPropertiesType() {
		return BitmapFontProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		// TODO Auto-generated method stub
	}

	@Override
	public BitmapFontData loadAsyncData(DependencyProvider provider, FileHandle file, BitmapFontProperties properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BitmapFont consumeAsyncData(DependencyProvider provider, FileHandle file, BitmapFontProperties properties,
			BitmapFontData asyncData) {
		// TODO Auto-generated method stub
		return null;
	}
}
