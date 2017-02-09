package com.gurella.engine.asset.loader.textureatlas;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Region;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class TextureAtlasLoader implements AssetLoader<TextureAtlasData, TextureAtlas, TextureAtlasProperties> {
	@Override
	public Class<TextureAtlasProperties> getAssetPropertiesType() {
		return TextureAtlasProperties.class;
	}

	@Override
	public TextureAtlasData init(DependencyCollector collector, FileHandle assetFile) {
		FileHandle imgDir = assetFile.parent();
		TextureAtlasData data = new TextureAtlasData(assetFile, imgDir, false);

		FileType fileType = assetFile.type();
		Array<Page> pages = data.getPages();
		for (int i = 0, n = pages.size; i < n; i++) {
			Page page = pages.get(i);
			collector.addDependency(page.textureFile.path(), fileType, Texture.class);
		}

		return data;
	}

	@Override
	public TextureAtlasData processAsync(DependencySupplier provider, FileHandle file, TextureAtlasData asyncData,
			TextureAtlasProperties properties) {
		FileType fileType = file.type();
		Array<Page> pages = asyncData.getPages();
		for (int i = 0, n = pages.size; i < n; i++) {
			Page page = pages.get(i);
			page.texture = provider.getDependency(page.textureFile.path(), fileType, Texture.class, null);
		}

		if (properties != null && properties.flip) {
			Array<Region> regions = asyncData.getRegions();
			for (int i = 0, n = regions.size; i < n; i++) {
				Region region = regions.get(i);
				region.flip = true;
			}
		}

		return asyncData;
	}

	@Override
	public TextureAtlas finish(DependencySupplier provider, FileHandle file, TextureAtlasData asyncData,
			TextureAtlasProperties properties) {
		return new TextureAtlas(asyncData);
	}
}
