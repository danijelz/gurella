package com.gurella.engine.desktop;

import com.badlogic.gdx.files.FileHandle;
import com.jcraft.jorbis.JOrbisException;
import com.jcraft.jorbis.VorbisFile;

public class JoggTest {
	public static void main(String[] args) throws JOrbisException {
		VorbisFile vorbisFile = new VorbisFile(
				"/media/danijel/data/ddd/gurella/runtime-EclipseApplication(1)/test/assets/music/ogg/Explosion20.ogg");
		System.out.println(vorbisFile.time_total(-1));
		
		FileHandle file = new FileHandle("/media/danijel/data/ddd/gurella/runtime-EclipseApplication(1)/test/assets/music/ogg/Explosion4.ogg");
		SmartStream smartStream = new SmartStream(file.readBytes());
		OVFile ovFile = new OVFile(smartStream);
		System.out.println(ovFile.time_total(-1));
	}
}
