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
	texture(Texture.class, false, true, "png", "jpg", "jpeg"),
	textureAtlas(TextureAtlas.class, false, true, "atl"),
	cubemap(Cubemap.class, "ktx", "zktx"),
	bitmapFont(BitmapFont.class, "fnt"),
	I18NBundle(I18NBundle.class, "i18n"),
	model(Model.class, false, true, "g3dj", "g3db", "obj"),
	music(Music.class, "wav", "ogg", "mp3", "scl"),
	sound(Sound.class, "wav", "ogg", "mp3", "scl"),
	soundClip(SoundClip.class, "wav", "ogg", "mp3", "scl"),
	audioClip(AudioClip.class, false, true, "wav", "ogg", "mp3", "scl"),
	pixmap(Pixmap.class, false, true, "png", "bmp", "jpg", "jpeg"),
	polygonRegion(PolygonRegion.class, "psh"),
	inputActionMap(InputActionMap.class, true, false, "giam"),
	prefab(SceneNode.class, true, false, "pref"),
	scene(Scene.class, true, false, "gscn"),
	material(MaterialDescriptor.class, true, false, "gmat"),
	shaderTemplate(ShaderTemplate.class, true, false, "glslt"),
	renderTarget(RenderTarget.class, true, false, "grt"),
	applicationConfig(ApplicationConfig.class, true, false, "gcfg"),
	assetProperties(ApplicationConfig.class, true, false, "gprop"),
	renderProgram(UnimplementedAsset.class),
	spritterAnimation(UnimplementedAsset.class),
	splineAnimation(UnimplementedAsset.class),
	svg(UnimplementedAsset.class),
	font(UnimplementedAsset.class),
	texture3d(UnimplementedAsset.class),
	particleSystem(UnimplementedAsset.class);

	public final Class<?> assetType;
	public final String[] extensions;
	public final boolean composite;
	public final boolean hasPropsFile;

	private AssetType(Class<?> assetType, String... extensions) {
		this(assetType, false, false, extensions);
	}

	private AssetType(Class<?> assetType, boolean composite, boolean hasPropsFile, String... extensions) {
		this.assetType = assetType;
		this.extensions = extensions;
		this.composite = composite;
		this.hasPropsFile = hasPropsFile;
		Arrays.sort(this.extensions);
	}

	public boolean isValidExtension(String extension) {
		return Arrays.binarySearch(extensions, extension) >= 0;
	}

	public String extension() {
		return extensions.length == 0 ? null : extensions[0];
	}

	private static final class UnimplementedAsset {
		private UnimplementedAsset() {
			throw new UnsupportedOperationException();
		}
	}
}
