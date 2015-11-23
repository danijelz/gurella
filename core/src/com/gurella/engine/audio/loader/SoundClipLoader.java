package com.gurella.engine.audio.loader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader.SoundParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectFloatMap;

public class SoundClipLoader extends AsynchronousAssetLoader<SoundClip, SoundClipLoader.SoundClipParameter> {
	private static final ObjectFloatMap<String> durationsByFilename = new ObjectFloatMap<String>(64);

	private static final SoundParameter soundParameter = new SoundParameter();
	private SoundLoader soundLoader;

	private float duration;

	public SoundClipLoader(FileHandleResolver resolver) {
		super(resolver);
		soundLoader = new SoundLoader(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, SoundClipParameter parameter) {
		soundLoader.loadAsync(manager, fileName, file, soundParameter);
		duration = getDuration(file);
	}

	protected float getDuration(FileHandle file) {
		String path = file.path();
		float duration = durationsByFilename.get(path, -1);
		if (duration < 0) {
			duration = SoundDuration.totalDuration(file);
			durationsByFilename.put(path, duration);
		}
		return duration;
	}

	@Override
	public SoundClip loadSync(AssetManager manager, String fileName, FileHandle file, SoundClipParameter parameter) {
		return new SoundClip(soundLoader.loadSync(manager, fileName, file, soundParameter), duration);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SoundClipParameter parameter) {
		return null;
	}

	static public class SoundClipParameter extends AssetLoaderParameters<SoundClip> {
	}
}
