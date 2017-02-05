package com.gurella.engine.asset2.loader.audio;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset2.loader.audio.mp3.Mp3File;
import com.gurella.engine.asset2.loader.audio.ogg.VorbisFile;
import com.gurella.engine.asset2.loader.audio.wav.WavFile;

public class SoundDuration {
	public static float totalDuration(String fileName) {
		return totalDuration(new FileHandle(fileName));
	}

	public static float totalDuration(FileHandle file) {
		String extension = file.extension().toLowerCase();
		if ("wav".equals(extension)) {
			return WavFile.totalDuration(file);
		} else if ("mp3".equals(extension)) {
			return Mp3File.totalDuration(file);
		} else if ("ogg".equals(extension)) {
			return VorbisFile.totalDuration(file);
		} else {
			throw new IllegalArgumentException();
		}
	}
}
