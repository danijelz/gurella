package com.gurella.engine.asset2.descriptor;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.gurella.engine.application.ApplicationConfig;
import com.gurella.engine.asset2.loader.AssetProperties;
import com.gurella.engine.asset2.loader.bitmapfont.BitmapFontLoader;
import com.gurella.engine.asset2.loader.cubemap.CubemapLoader;
import com.gurella.engine.asset2.loader.i18nbundle.I18NBundleLoader;
import com.gurella.engine.asset2.loader.json.SelializedJsonLoader;
import com.gurella.engine.asset2.loader.model.JsonG3dModelLoader;
import com.gurella.engine.asset2.loader.model.ObjModelLoader;
import com.gurella.engine.asset2.loader.model.UbJsonG3dModelLoader;
import com.gurella.engine.asset2.loader.music.MusicLoader;
import com.gurella.engine.asset2.loader.pixmap.PixmapLoader;
import com.gurella.engine.asset2.loader.rendertarget.RenderTargetLoader;
import com.gurella.engine.asset2.loader.sound.SoundLoader;
import com.gurella.engine.asset2.loader.texture.TextureLoader;
import com.gurella.engine.asset2.loader.textureatlas.TextureAtlasLoader;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.graphics.render.RenderTarget;
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
		array.add(new AssetDescriptor<RenderTarget>(RenderTarget.class, true, true, new RenderTargetLoader(), "grt"));

		array.add(new AssetDescriptor<Texture>(Texture.class, false, false, new TextureLoader(), "png", "jpg", "jpeg"));
		array.add(new AssetDescriptor<TextureAtlas>(TextureAtlas.class, false, false, new TextureAtlasLoader(), "atl"));
		array.add(new AssetDescriptor<Cubemap>(Cubemap.class, false, false, new CubemapLoader(), "ktx", "zktx"));
		array.add(new AssetDescriptor<BitmapFont>(BitmapFont.class, false, false, new BitmapFontLoader(), "fnt"));
		array.add(new AssetDescriptor<I18NBundle>(I18NBundle.class, false, false, new I18NBundleLoader(), "i18n"));
		array.add(new AssetDescriptor<Sound>(Sound.class, false, false, new SoundLoader(), "wav", "ogg", "mp3"));
		array.add(new AssetDescriptor<Music>(Music.class, false, false, new MusicLoader(), "wav", "ogg", "mp3"));
		array.add(new AssetDescriptor<Model>(Model.class, false, false, new ObjModelLoader(), "obj"));
		array.add(new AssetDescriptor<Model>(Model.class, false, false, new JsonG3dModelLoader(), "g3dj"));
		array.add(new AssetDescriptor<Model>(Model.class, false, false, new UbJsonG3dModelLoader(), "g3db"));
		array.add(new AssetDescriptor<Pixmap>(Pixmap.class, false, false, new PixmapLoader(), "png", "bmp", "jpg",
				"jpeg"));
		//PolygonRegionLoader
		//ShaderTemplateLoader
		//		soundClip(SoundClip.class, "wav", "ogg", "mp3", "scl"),
		//		audioClip(AudioClip.class, false, AudioClipProperties.class, "wav", "ogg", "mp3", "scl"),
		//		polygonRegion(PolygonRegion.class, "psh"),
		//		inputActionMap(InputActionMap.class, true, null, "giam"),
		//		shaderTemplate(ShaderTemplate.class, true, null, "glslt"),
		//		renderProgram(UnimplementedAsset.class),
		//		spritterAnimation(UnimplementedAsset.class),
		//		splineAnimation(UnimplementedAsset.class),
		//		svg(UnimplementedAsset.class),
		//		font(UnimplementedAsset.class),
		//		texture3d(UnimplementedAsset.class),
		//		particleSystem(UnimplementedAsset.class);
		// TODO add loaders and persisters

		return array;
	}

	private static <T> AssetDescriptor<T> newJsonLoaderDescriptor(Class<T> type, String extension) {
		return new AssetDescriptor<T>(type, true, true, new SelializedJsonLoader<T>(type), extension);
	}
}
