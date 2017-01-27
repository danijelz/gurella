package com.gurella.engine.asset;

import java.util.Arrays;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.I18NBundle;
import com.gurella.engine.application.ApplicationConfig;
import com.gurella.engine.asset2.properties.AssetProperties;
import com.gurella.engine.asset2.properties.AudioClipProperties;
import com.gurella.engine.asset2.properties.ModelProperties;
import com.gurella.engine.asset2.properties.PixmapProperties;
import com.gurella.engine.asset2.properties.TextureAtlasProperties;
import com.gurella.engine.asset2.properties.TextureProperties;
import com.gurella.engine.audio.AudioClip;
import com.gurella.engine.audio.SoundClip;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.graphics.render.RenderTarget;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;
import com.gurella.engine.input.InputActionMap;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;

//TODO convert to plugins
public enum AssetType {
	texture(Texture.class, false, TextureProperties.class, "png", "jpg", "jpeg"),
	textureAtlas(TextureAtlas.class, false, TextureAtlasProperties.class, "atl"),
	cubemap(Cubemap.class, "ktx", "zktx"),
	bitmapFont(BitmapFont.class, "fnt"),
	I18NBundle(I18NBundle.class, "i18n"),
	model(Model.class, false, ModelProperties.class, "g3dj", "g3db", "obj"),
	music(Music.class, "wav", "ogg", "mp3", "scl"),
	sound(Sound.class, "wav", "ogg", "mp3", "scl"),
	soundClip(SoundClip.class, "wav", "ogg", "mp3", "scl"),
	audioClip(AudioClip.class, false, AudioClipProperties.class, "wav", "ogg", "mp3", "scl"),
	pixmap(Pixmap.class, false, PixmapProperties.class, "png", "bmp", "jpg", "jpeg"),
	polygonRegion(PolygonRegion.class, "psh"),
	inputActionMap(InputActionMap.class, true, null, "giam"),
	prefab(SceneNode.class, true, null, "pref"),
	scene(Scene.class, true, null, "gscn"),
	material(MaterialDescriptor.class, true, null, "gmat"),
	shaderTemplate(ShaderTemplate.class, true, null, "glslt"),
	renderTarget(RenderTarget.class, true, null, "grt"),
	applicationConfig(ApplicationConfig.class, true, null, "gcfg"),
	assetProperties(AssetProperties.class, true, null, "gprop"),
	renderProgram(UnimplementedAsset.class),
	spritterAnimation(UnimplementedAsset.class),
	splineAnimation(UnimplementedAsset.class),
	svg(UnimplementedAsset.class),
	font(UnimplementedAsset.class),
	texture3d(UnimplementedAsset.class),
	particleSystem(UnimplementedAsset.class);

	public final Class<?> assetType;
	public final String[] fileExtensions;
	public final boolean composite;
	public final Class<? extends AssetProperties<?>> propsType;

	private AssetType(Class<?> assetType, String... extensions) {
		this(assetType, false, null, extensions);
	}

	private AssetType(Class<?> assetType, boolean composite, Class<? extends AssetProperties<?>> propsType,
			String... extensions) {
		this.assetType = assetType;
		this.fileExtensions = extensions;
		this.composite = composite;
		this.propsType = propsType;
		Arrays.sort(this.fileExtensions);
	}

	public boolean isValidExtension(String extension) {
		return Arrays.binarySearch(fileExtensions, extension) >= 0;
	}

	public String extension() {
		return fileExtensions.length == 0 ? null : fileExtensions[0];
	}

	private static final class UnimplementedAsset {
		private UnimplementedAsset() {
			throw new UnsupportedOperationException();
		}
	}
}
