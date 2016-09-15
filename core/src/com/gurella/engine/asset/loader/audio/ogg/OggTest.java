package com.gurella.engine.asset.loader.audio.ogg;

import com.badlogic.gdx.files.FileHandle;

public class OggTest {
	public static void main(String[] args) {
		FileHandle file = new FileHandle("/media/danijel/data/ddd/gurella/runtime-EclipseApplication(1)/test/assets/music/ogg/Explosion4.ogg");
		VorbisFile vorbisFile = new VorbisFile();
		vorbisFile.init(file);
		System.out.println(vorbisFile.time_total());
	}
}
