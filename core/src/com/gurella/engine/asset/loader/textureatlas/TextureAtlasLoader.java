package com.gurella.engine.asset.loader.textureatlas;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Region;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class TextureAtlasLoader extends BaseAssetLoader<TextureAtlas, TextureAtlasProperties> {
	@Override
	public Class<TextureAtlasProperties> getPropertiesType() {
		return TextureAtlasProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		FileHandle imgDir = assetFile.parent();
		TextureAtlasData textureAtlasData = new TextureAtlasData(assetFile, imgDir, false);

		FileType fileType = assetFile.type();
		Array<Page> pages = textureAtlasData.getPages();
		for (int i = 0, n = pages.size; i < n; i++) {
			Page page = pages.get(i);
			collector.addDependency(page.textureFile.path(), fileType, Texture.class);
		}
		put(assetFile, textureAtlasData);
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle assetFile, TextureAtlasProperties properties) {
		TextureAtlasData textureAtlasData = get(assetFile);
		FileType fileType = assetFile.type();
		Array<Page> pages = textureAtlasData.getPages();
		for (int i = 0, n = pages.size; i < n; i++) {
			Page page = pages.get(i);
			page.texture = provider.getDependency(page.textureFile.path(), fileType, Texture.class, null);
		}

		if (properties != null && properties.flip) {
			Array<Region> regions = textureAtlasData.getRegions();
			for (int i = 0, n = regions.size; i < n; i++) {
				Region region = regions.get(i);
				region.flip = true;
			}
		}
	}

	@Override
	public TextureAtlas finish(DependencySupplier provider, FileHandle assetFile, TextureAtlasProperties properties) {
		TextureAtlasData textureAtlasData = remove(assetFile);
		return new TextureAtlas(textureAtlasData);
	}
}
