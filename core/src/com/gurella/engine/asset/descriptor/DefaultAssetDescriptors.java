package com.gurella.engine.asset.descriptor;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
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
import com.gurella.engine.asset.loader.polygonregion.PolygonRegionLoader;
import com.gurella.engine.asset.loader.rendertarget.RenderTargetLoader;
import com.gurella.engine.asset.loader.sound.SoundLoader;
import com.gurella.engine.asset.loader.texture.TextureLoader;
import com.gurella.engine.asset.loader.textureatlas.TextureAtlasLoader;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.factory.Factory;
import com.gurella.engine.utils.factory.ReflectionFactory;

public class DefaultAssetDescriptors {
	//@formatter:off
	static final Array<AssetDescriptor<?>> _descriptors = new Array<AssetDescriptor<?>>();
	public static final ImmutableArray<AssetDescriptor<?>> descriptors = new ImmutableArray<AssetDescriptor<?>>(_descriptors);

	public static final AssetDescriptor<Scene> scene = createSerialized(Scene.class, "gscn");
	public static final AssetDescriptor<SceneNode> prefab = createSerialized(SceneNode.class, "pref");
	public static final AssetDescriptor<MaterialDescriptor> material = createSerialized(MaterialDescriptor.class, "gmat");
	public static final AssetDescriptor<ApplicationConfig> appConfig = createSerialized(ApplicationConfig.class, "gcfg");
	public static final AssetDescriptor<AssetProperties> assetProps = createSerialized(AssetProperties.class, "gprop");
	public static final AssetDescriptor<RenderTarget> renderTarget = create(RenderTarget.class, true, true, RenderTargetLoader.class, "grt");

	public static final AssetDescriptor<Texture> texture = create(Texture.class, false, false, TextureLoader.class, "png", "jpg", "jpeg");
	public static final AssetDescriptor<TextureAtlas> textureAtlas = create(TextureAtlas.class, false, false, TextureAtlasLoader.class, "atl");
	public static final AssetDescriptor<Cubemap> cubemap = create(Cubemap.class, false, false, CubemapLoader.class, "ktx", "zktx");
	public static final AssetDescriptor<BitmapFont> bitmapFont = create(BitmapFont.class, false, false, BitmapFontLoader.class, "fnt");
	public static final AssetDescriptor<I18NBundle> i18NBundle = create(I18NBundle.class, false, false, I18NBundleLoader.class, "i18n");
	public static final AssetDescriptor<Sound> sound = create(Sound.class, false, false, SoundLoader.class, "wav", "ogg", "mp3");
	public static final AssetDescriptor<Music> music = create(Music.class, false, false, MusicLoader.class, "wav", "ogg", "mp3");
	public static final AssetDescriptor<Pixmap> pixmap = create(Pixmap.class, false, false, PixmapLoader.class, "png", "bmp", "jpg", "jpeg");
	public static final AssetDescriptor<PolygonRegion> polygonRegion = create(PolygonRegion.class, false, false, PolygonRegionLoader.class, "psh");
	public static final AssetDescriptor<Model> model = createModelDescriptor();
	//@formatter:on

	// ShaderTemplateLoader
	// soundClip(SoundClip.class, "wav", "ogg", "mp3", "scl"),
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

	private DefaultAssetDescriptors() {
	}

	private static <T> AssetDescriptor<T> createSerialized(Class<T> type, String extension) {
		AssetDescriptor<T> descriptor = new AssetDescriptor<T>(type, true, true);
		descriptor.registerLoaderFactory(new SelializedJsonLoaderFactory<T>(type), extension);
		_descriptors.add(descriptor);
		return descriptor;
	}

	private static <T, L extends AssetLoader<T, ? extends AssetProperties>> AssetDescriptor<T> create(
			Class<T> assetType, boolean subtypes, boolean references, Class<L> loaderType, String extension) {
		AssetDescriptor<T> descriptor = new AssetDescriptor<T>(assetType, subtypes, references);
		descriptor.registerLoaderFactory(new ReflectionFactory<L>(loaderType), extension);
		_descriptors.add(descriptor);
		return descriptor;
	}

	private static <T, L extends AssetLoader<T, ? extends AssetProperties>> AssetDescriptor<T> create(
			Class<T> assetType, boolean subtypes, boolean references, Class<L> loaderType, String... extensions) {
		AssetDescriptor<T> descriptor = new AssetDescriptor<T>(assetType, subtypes, references);
		descriptor.registerLoaderFactory(new ReflectionFactory<L>(loaderType), extensions);
		_descriptors.add(descriptor);
		return descriptor;
	}

	private static AssetDescriptor<Model> createModelDescriptor() {
		AssetDescriptor<Model> descriptor = new AssetDescriptor<Model>(Model.class, false, false);
		descriptor.registerLoaderFactory(new ReflectionFactory<ObjModelLoader>(ObjModelLoader.class), "obj");
		descriptor.registerLoaderFactory(new ReflectionFactory<JsonG3dModelLoader>(JsonG3dModelLoader.class), "g3dj");
		descriptor.registerLoaderFactory(new ReflectionFactory<UbJsonG3dModelLoader>(UbJsonG3dModelLoader.class),
				"g3db");
		_descriptors.add(descriptor);
		return descriptor;
	}

	private static class SelializedJsonLoaderFactory<T> implements Factory<SelializedJsonLoader<T>> {
		private final Class<T> expectedType;

		SelializedJsonLoaderFactory(Class<T> expectedType) {
			this.expectedType = expectedType;
		}

		@Override
		public SelializedJsonLoader<T> create() {
			return new SelializedJsonLoader<T>(expectedType);
		}
	}
}
