package com.gurella.engine.asset.loader.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencyProvider;

public class SoundLoader implements AssetLoader<Sound, Sound, SoundProperties> {
	@Override
	public Class<SoundProperties> getAssetPropertiesType() {
		return SoundProperties.class;
	}

	@Override
	public Sound init(DependencyCollector collector, FileHandle assetFile) {
		return null;
	}

	@Override
	public Sound processAsync(DependencyProvider provider, FileHandle file, Sound asyncData,
			SoundProperties properties) {
		return Gdx.audio.newSound(file);
	}

	@Override
	public Sound finish(DependencyProvider provider, FileHandle file, Sound asyncData,
			SoundProperties properties) {
		return asyncData;
	}
}
