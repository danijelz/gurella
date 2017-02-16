package com.gurella.engine.asset.loader.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class MusicLoader extends BaseAssetLoader<Music, MusicProperties> {
	@Override
	public Class<MusicProperties> getPropertiesType() {
		return MusicProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle assetFile, MusicProperties properties) {
		put(assetFile, Gdx.audio.newMusic(assetFile));
	}

	@Override
	public Music finish(DependencySupplier provider, FileHandle assetFile, MusicProperties properties) {
		return remove(assetFile);
	}
}
