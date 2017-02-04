package com.gurella.engine.asset2.loader.textureatlas;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.asset2.loader.AssetLoader;
import com.gurella.engine.asset2.loader.DependencyCollector;
import com.gurella.engine.asset2.loader.DependencyProvider;

public class TextureAtlasLoader implements AssetLoader<TextureAtlasData, TextureAtlas, TextureAtlasProperties> {
	@Override
	public Class<TextureAtlasProperties> getAssetPropertiesType() {
		return TextureAtlasProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		FileHandle imgDir = assetFile.parent();

		/*if (parameter != null)
			data = new TextureAtlasData(atlasFile, imgDir, parameter.flip);
		else {
			data = new TextureAtlasData(atlasFile, imgDir, false);
		}

		Array<AssetDescriptor> dependencies = new Array();
		for (Page page : data.getPages()) {
			dependencies.add(new AssetDescriptor(page.textureFile, Texture.class, params));
		}*/
		// TODO Auto-generated method stub
	}

	@Override
	public TextureAtlasData loadAsyncData(DependencyProvider provider, FileHandle file,
			TextureAtlasProperties properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextureAtlas consumeAsyncData(DependencyProvider provider, FileHandle file,
			TextureAtlasProperties properties, TextureAtlasData asyncData) {
		for (Page page : asyncData.getPages()) {
			String texturePath = page.textureFile.path().replaceAll("\\\\", "/");
			page.texture = provider.getDependency(texturePath, file.type(), Texture.class);
		}

		return new TextureAtlas(asyncData);
	}
}
