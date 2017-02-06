package com.gurella.engine.asset.descriptor;

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
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.asset.loader.bitmapfont.BitmapFontLoader;
import com.gurella.engine.asset.loader.cubemap.CubemapLoader;
import com.gurella.engine.asset.loader.i18nbundle.I18NBundleLoader;
import com.gurella.engine.asset.loader.json.SelializedJsonLoader;
import com.gurella.engine.asset.loader.model.JsonG3dModelLoader;
import com.gurella.engine.asset.loader.model.ObjModelLoader;
import com.gurella.engine.asset.loader.model.UbJsonG3dModelLoader;
import com.gurella.engine.asset.loader.music.MusicLoader;
import com.gurella.engine.asset.loader.pixmap.PixmapLoader;
import com.gurella.engine.asset.loader.rendertarget.RenderTargetLoader;
import com.gurella.engine.asset.loader.sound.SoundLoader;
import com.gurella.engine.asset.loader.texture.TextureLoader;
import com.gurella.engine.asset.loader.textureatlas.TextureAtlasLoader;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.utils.ImmutableArray;

public class DefaultAssetDescriptors {
	final Array<AssetDescriptor<?>> _descriptors = new Array<AssetDescriptor<?>>();
	public final ImmutableArray<AssetDescriptor<?>> descriptors = new ImmutableArray<AssetDescriptor<?>>(_descriptors);

	public final AssetDescriptor<Scene> scene;
	public final AssetDescriptor<SceneNode> prefab;
	public final AssetDescriptor<MaterialDescriptor> material;
	public final AssetDescriptor<ApplicationConfig> appConfig;
	public final AssetDescriptor<AssetProperties> assetProps;
	public final AssetDescriptor<RenderTarget> renderTarget;

	public final AssetDescriptor<Texture> texture;
	public final AssetDescriptor<TextureAtlas> textureAtlas;
	public final AssetDescriptor<Cubemap> cubemap;
	public final AssetDescriptor<BitmapFont> bitmapFont;
	public final AssetDescriptor<I18NBundle> i18NBundle;
	public final AssetDescriptor<Sound> sound;
	public final AssetDescriptor<Music> music;
	public final AssetDescriptor<Pixmap> pixmap;
	public final AssetDescriptor<Model> model;

	DefaultAssetDescriptors() {
		scene = create(Scene.class, "gscn");
		_descriptors.add(scene);

		prefab = create(SceneNode.class, "pref");
		_descriptors.add(prefab);

		material = create(MaterialDescriptor.class, "gmat");
		_descriptors.add(material);

		appConfig = create(ApplicationConfig.class, "gcfg");
		_descriptors.add(appConfig);

		assetProps = create(AssetProperties.class, "gprop");
		_descriptors.add(assetProps);

		renderTarget = create(RenderTarget.class, true, true, new RenderTargetLoader(), "grt");
		_descriptors.add(renderTarget);

		texture = create(Texture.class, false, false, new TextureLoader(), "png", "jpg", "jpeg");
		_descriptors.add(texture);

		textureAtlas = create(TextureAtlas.class, false, false, new TextureAtlasLoader(), "atl");
		_descriptors.add(textureAtlas);

		cubemap = create(Cubemap.class, false, false, new CubemapLoader(), "ktx", "zktx");
		_descriptors.add(cubemap);

		bitmapFont = create(BitmapFont.class, false, false, new BitmapFontLoader(), "fnt");
		_descriptors.add(bitmapFont);

		i18NBundle = create(I18NBundle.class, false, false, new I18NBundleLoader(), "i18n");
		_descriptors.add(i18NBundle);

		sound = create(Sound.class, false, false, new SoundLoader(), "wav", "ogg", "mp3");
		_descriptors.add(sound);

		music = create(Music.class, false, false, new MusicLoader(), "wav", "ogg", "mp3");
		_descriptors.add(music);

		pixmap = create(Pixmap.class, false, false, new PixmapLoader(), "png", "bmp", "jpg", "jpeg");
		_descriptors.add(pixmap);

		model = create(Model.class, false, false, new ObjModelLoader(), "obj");
		model.registerLoader(new JsonG3dModelLoader(), "g3dj");
		model.registerLoader(new UbJsonG3dModelLoader(), "g3db");
		_descriptors.add(model);

		// PolygonRegionLoader
		// ShaderTemplateLoader
		// soundClip(SoundClip.class, "wav", "ogg", "mp3", "scl"),
		// audioClip(AudioClip.class, false, AudioClipProperties.class, "wav", "ogg", "mp3", "scl"),
		// polygonRegion(PolygonRegion.class, "psh"),
		// inputActionMap(InputActionMap.class, true, null, "giam"),
		// shaderTemplate(ShaderTemplate.class, true, null, "glslt"),
		// renderProgram(UnimplementedAsset.class),
		// spritterAnimation(UnimplementedAsset.class),
		// splineAnimation(UnimplementedAsset.class),
		// svg(UnimplementedAsset.class),
		// font(UnimplementedAsset.class),
		// texture3d(UnimplementedAsset.class),
		// particleSystem(UnimplementedAsset.class);
		// TODO add loaders and persisters
	}

	private static <TYPE> AssetDescriptor<TYPE> create(Class<TYPE> type, String extension) {
		return create(type, true, true, new SelializedJsonLoader<TYPE>(type), extension);
	}

	private static <TYPE> AssetDescriptor<TYPE> create(Class<TYPE> assetType, boolean validForSubtypes,
			boolean hasReferences, AssetLoader<?, TYPE, ? extends AssetProperties> loader, String... extensions) {
		return new AssetDescriptor<TYPE>(assetType, validForSubtypes, hasReferences, loader, extensions);
	}
}
