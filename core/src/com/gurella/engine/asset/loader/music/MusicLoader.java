package com.gurella.engine.asset.loader.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class MusicLoader implements AssetLoader<Music, MusicProperties> {
	private Music music;

	@Override
	public Class<MusicProperties> getPropertiesType() {
		return MusicProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle file, MusicProperties properties) {
		music = Gdx.audio.newMusic(file);
	}

	@Override
	public Music finish(DependencySupplier provider, FileHandle file, MusicProperties properties) {
		try {
			return music;
		} finally {
			music = null;
		}
	}
}
