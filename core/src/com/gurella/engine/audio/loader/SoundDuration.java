package com.gurella.engine.audio.loader;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.audio.loader.mp3.Mp3File;
import com.gurella.engine.audio.loader.ogg.VorbisFile;
import com.gurella.engine.audio.loader.wav.WavFile;

public class SoundDuration {
	/*public static void main(String[] args) {
		float duration = totalDuration("/home/danijel/Music/BigExplosion.wav");
		BigDecimal decimal = new BigDecimal(duration);
		System.out.println(decimal.toPlainString());

		duration = totalDuration("/home/danijel/Music/OpenDialog.mp3");
		decimal = new BigDecimal(duration);
		System.out.println(decimal.toPlainString());

		duration = totalDuration("/home/danijel/Music/orc_pain.ogg");
		decimal = new BigDecimal(duration);
		System.out.println(decimal.toPlainString());
	}*/

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
