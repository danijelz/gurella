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
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.audio.loader.SoundClip;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.input.InputActionMap;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;

public enum AssetType {
	texture(Texture.class, "png", "jpg", "jpeg"),
	textureAtlas(TextureAtlas.class, "atl"),
	cubemap(Cubemap.class),
	bitmapFont(BitmapFont.class, "fnt"),
	I18NBundle(I18NBundle.class),
	model(Model.class, "g3dj", "g3db", "obj"),
	music(Music.class, "wav", "ogg", "mp3", "scl"),
	sound(Sound.class, "wav", "ogg", "mp3", "scl"),
	soundClip(SoundClip.class, "wav", "ogg", "mp3", "scl"),
	pixmap(Pixmap.class, "pix", "bmp"),
	polygonRegion(PolygonRegion.class, "psh"),
	inputActionMap(InputActionMap.class, "giam"),
	prefab(SceneNode2.class, "pref"),
	scene(Scene.class, "gscn"),
	material(MaterialDescriptor.class, "gmtl"),
	renderProgram(null),
	spritterAnimation(null),
	splineAnimation(null),
	svg(null),
	font(null),
	texture3d(null),
	renderTexture(null),
	particleSystem(null);

	private static ObjectMap<Class<?>, AssetType> enumsByType = new ObjectMap<Class<?>, AssetType>();

	static {
		AssetType[] values = values();
		for (int i = 0, n = values.length; i < n; i++) {
			AssetType type = values[i];
			if (type.assetType != null) {
				enumsByType.put(type.assetType, type);
			}
		}
	}

	public final Class<?> assetType;
	public final String[] extensions;

	private AssetType(Class<?> assetType, String... extensions) {
		this.assetType = assetType;
		this.extensions = extensions;
		Arrays.sort(this.extensions);
	}

	public boolean containsExtension(String extension) {
		return Arrays.binarySearch(extensions, extension) >= 0;
	}

	public static boolean isValidExtension(Class<?> assetType, String extension) {
		AssetType type = value(assetType);
		return type != null && Arrays.binarySearch(type.extensions, extension) >= 0;
	}

	public static AssetType value(Class<?> assetType) {
		return enumsByType.get(assetType);
	}
}
