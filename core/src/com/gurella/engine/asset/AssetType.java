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
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.audio.loader.SoundClip;

public enum AssetType {
	texture(Texture.class, "bmp", "jpg", "jpeg", "png"),
	textureAtlas(TextureAtlas.class, "atl"),
	cubemap(Cubemap.class),
	bitmapFont(BitmapFont.class),
	I18NBundle(I18NBundle.class),
	model(Model.class),
	music(Music.class, "wav", "ogg", "mp3"),
	sound(Sound.class, "wav", "ogg", "mp3"),
	soundClip(SoundClip.class, "wav", "ogg", "mp3"),
	json(JsonValue.class, "json"),
	pixmap(Pixmap.class),
	polygonRegion(PolygonRegion.class),
	renderProgram(null),
	material(null),
	spritter(null),
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
		for (int i = 0, n = extensions.length; i < n; i++) {
			if (extensions[i].equals(extension)) {
				return true;
			}
		}
		return false;
	}
}
