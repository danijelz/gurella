package com.gurella.engine.asset2.descriptor;

import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.application.ApplicationConfig;
import com.gurella.engine.asset2.loader.AssetProperties;
import com.gurella.engine.asset2.loader.bitmapfont.BitmapFontLoader;
import com.gurella.engine.asset2.loader.cubemap.CubemapLoader;
import com.gurella.engine.asset2.loader.json.SelializedJsonLoader;
import com.gurella.engine.asset2.loader.texture.TextureLoader;
import com.gurella.engine.asset2.loader.textureatlas.TextureAtlasLoader;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;

public class DefaultAssetDescriptors {
	public static Array<AssetDescriptor<?>> create() {
		Array<AssetDescriptor<?>> array = new Array<AssetDescriptor<?>>();
		array.add(newJsonLoaderDescriptor(Scene.class, "gscn"));
		array.add(newJsonLoaderDescriptor(SceneNode.class, "pref"));
		array.add(newJsonLoaderDescriptor(MaterialDescriptor.class, "gmat"));
		array.add(newJsonLoaderDescriptor(ApplicationConfig.class, "gcfg"));
		array.add(newJsonLoaderDescriptor(AssetProperties.class, "gprop"));

		array.add(new AssetDescriptor<Texture>(Texture.class, false, false, new TextureLoader(), "png", "jpg", "jpeg"));
		array.add(new AssetDescriptor<TextureAtlas>(TextureAtlas.class, false, false, new TextureAtlasLoader(), "atl"));
		array.add(new AssetDescriptor<Cubemap>(Cubemap.class, false, false, new CubemapLoader(), "ktx", "zktx"));
		array.add(new AssetDescriptor<BitmapFont>(BitmapFont.class, false, false, new BitmapFontLoader(), "fnt"));

		// TODO add loaders
		return array;
	}

	private static <T> AssetDescriptor<T> newJsonLoaderDescriptor(Class<T> type, String extension) {
		return new AssetDescriptor<T>(type, true, true, new SelializedJsonLoader<T>(type), extension);
	}
}
