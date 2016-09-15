package com.gurella.engine.desktop;

import com.jcraft.jorbis.JOrbisException;
import com.jcraft.jorbis.VorbisFile;

public class JoggTest {
	public static void main(String[] args) throws JOrbisException {
		VorbisFile vorbisFile = new VorbisFile(
				"/media/danijel/data/ddd/gurella/runtime-EclipseApplication(1)/test/assets/music/ogg/Explosion20.ogg");
		System.out.println(vorbisFile.time_total(-1));
	}
}
