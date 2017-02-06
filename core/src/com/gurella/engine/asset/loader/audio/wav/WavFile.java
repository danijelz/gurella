package com.gurella.engine.asset.loader.audio.wav;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.StreamUtils;

public class WavFile implements Poolable {
	private final WawHeader header = new WawHeader();
	private InputStream stream;

	public static float totalDuration(FileHandle file) {
		WavFile wavFile = Pools.obtain(WavFile.class);
		try {
			wavFile.init(file.read());
			return wavFile.totalDuration();
		} finally {
			Pools.free(wavFile);
		}
	}

	private WavFile() {
	}

	public void init(InputStream stream) {
		this.stream = stream;
	}

	private float totalDuration() {
		try {
			if (stream.read(header.array, 0, 44) != 44) {
				return 0;
			}
		} catch (IOException e) {
			throw new GdxRuntimeException("Error reading wav file.");
		}

		int numSamples = header.getSubchunk2Size() / (header.getNumChannels() * (header.getBitsPerSample() / 8));
		return (float) numSamples / header.getSampleRate();
	}

	@Override
	public void reset() {
		StreamUtils.closeQuietly(stream);
		stream = null;
	}

	private static class WawHeader {
		byte[] array = new byte[44];
		ByteBuffer buffer = ByteBuffer.wrap(array);

		public WawHeader() {
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		}

		public int getSubchunk2Size() {
			return buffer.getInt(40);
		}

		public short getNumChannels() {
			return buffer.getShort(22);
		}

		public short getBitsPerSample() {
			return buffer.getShort(34);
		}

		public int getSampleRate() {
			return buffer.getInt(24);
		}
	}
}
