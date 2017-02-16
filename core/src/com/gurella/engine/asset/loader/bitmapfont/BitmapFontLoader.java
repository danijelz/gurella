package com.gurella.engine.asset.loader.bitmapfont;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class BitmapFontLoader extends BaseAssetLoader<BitmapFont, BitmapFontProperties> {
	@Override
	public Class<BitmapFontProperties> getPropertiesType() {
		return BitmapFontProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		BitmapFontData bitmapFontData = new BitmapFontData(assetFile, false);
		FileType fileType = assetFile.type();
		String[] imagePaths = bitmapFontData.getImagePaths();
		for (int i = 0, n = imagePaths.length; i < n; i++) {
			collector.addDependency(imagePaths[i], fileType, Texture.class);
		}
		put(assetFile, bitmapFontData);
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle assetFile, BitmapFontProperties properties) {
		BitmapFontData bitmapFontData = get(assetFile);
		bitmapFontData.flipped = properties != null && properties.flip;
	}

	@Override
	public BitmapFont finish(DependencySupplier provider, FileHandle assetFile, BitmapFontProperties properties) {
		BitmapFontData bitmapFontData = remove(assetFile);
		FileType fileType = assetFile.type();
		String[] imagePaths = bitmapFontData.getImagePaths();
		int length = imagePaths.length;
		Array<TextureRegion> regs = new Array<TextureRegion>(length);
		for (int i = 0; i < length; i++) {
			regs.add(new TextureRegion(provider.getDependency(imagePaths[i], fileType, Texture.class, null)));
		}
		return new BitmapFont(bitmapFontData, regs, true);
	}
}
