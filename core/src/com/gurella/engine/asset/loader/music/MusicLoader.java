package com.gurella.engine.asset.loader.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencyProvider;

public class MusicLoader implements AssetLoader<Music, Music, MusicProperties> {
	@Override
	public Class<MusicProperties> getAssetPropertiesType() {
		return MusicProperties.class;
	}

	@Override
	public Music init(DependencyCollector collector, FileHandle assetFile) {
		return null;
	}

	@Override
	public Music processAsync(DependencyProvider provider, FileHandle file, Music asyncData,
			MusicProperties properties) {
		return Gdx.audio.newMusic(file);
	}

	@Override
	public Music finish(DependencyProvider provider, FileHandle file, Music asyncData, MusicProperties properties) {
		return asyncData;
	}
}
