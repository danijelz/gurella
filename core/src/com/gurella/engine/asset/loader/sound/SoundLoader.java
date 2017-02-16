package com.gurella.engine.asset.loader.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class SoundLoader extends BaseAssetLoader<Sound, SoundProperties> {
	@Override
	public Class<SoundProperties> getPropertiesType() {
		return SoundProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle assetFile, SoundProperties properties) {
		put(assetFile, Gdx.audio.newSound(assetFile));
	}

	@Override
	public Sound finish(DependencySupplier provider, FileHandle assetFile, SoundProperties properties) {
		return remove(assetFile);
	}
}
