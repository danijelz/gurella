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
import com.gurella.engine.audio.loader.SoundClip;
import com.gurella.engine.input.InputActionMap;
import com.gurella.engine.scene.SceneNode2;

public enum AssetType {
	texture(Texture.class, "bmp", "jpg", "jpeg", "png"),
	textureAtlas(TextureAtlas.class, "atl"),
	cubemap(Cubemap.class),
	bitmapFont(BitmapFont.class, "fnt"),
	I18NBundle(I18NBundle.class),
	model(Model.class, "g3dj", "g3db", "obj"),
	music(Music.class, "wav", "ogg", "mp3", "scl"),
	sound(Sound.class, "wav", "ogg", "mp3", "scl"),
	soundClip(SoundClip.class, "wav", "ogg", "mp3", "scl"),
	pixmap(Pixmap.class, "pix"),
	polygonRegion(PolygonRegion.class, "psh"),
	inputActionMap(InputActionMap.class, "iam"),
	prefab(SceneNode2.class, "pref"),
	renderProgram(null),
	material(null),
	spritter(null),
	spline(null),
	svg(null),
	font(null),
	texture3d(null),
	renderTexture(null),
	particleSystem(null);

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
		AssetType[] values = values();
		for (int i = 0, n = values.length; i < n; i++) {
			AssetType type = values[i];
			if (type.assetType == assetType) {
				return Arrays.binarySearch(type.extensions, extension) >= 0;
			}
		}
		return false;
	}
}
