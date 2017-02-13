package com.gurella.engine.asset.loader.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class SoundLoader implements AssetLoader<Sound, SoundProperties> {
	private Sound sound;

	@Override
	public Class<SoundProperties> getPropertiesType() {
		return SoundProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle file, SoundProperties properties) {
		sound = Gdx.audio.newSound(file);
	}

	@Override
	public Sound finish(DependencySupplier provider, FileHandle file, SoundProperties properties) {
		try {
			return sound;
		} finally {
			sound = null;
		}
	}
}
