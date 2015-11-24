package com.gurella.engine.graphics.vector.sfnt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;

//TODO rename to SfntFile
public class RandomAccessFile implements Disposable {
	//private FileHandle fileHandle; //TODO write and save methods
	private int length;
	private ByteBuffer byteBuffer;

	public RandomAccessFile(FileHandle fileHandle) {
		//this.fileHandle = fileHandle;
		byte[] bytes = fileHandle.readBytes();
		length = bytes.length;
		byteBuffer = BufferUtils.newByteBuffer(length);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.put(bytes);
		byteBuffer.rewind();
	}
	
	public RandomAccessFile(byte[] bytes) {
		length = bytes.length;
		byteBuffer = BufferUtils.newByteBuffer(length);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.put(bytes);
		byteBuffer.rewind();
	}

	public int getLength() {
		return length;
	}

	public int getPosition() {
		return byteBuffer.position();
	}
	
	public void setPosition(int newPosition) {
		byteBuffer.position(newPosition);
	}
	
	public int skipBytes(int n) {
		if (n <= 0) {
			return 0;
		}

		int pos = getPosition();
		int len = getLength();
		int newpos = pos + n;
		
		if (newpos > len) {
			newpos = len;
		}
		
		setPosition(newpos);
		return newpos - pos;
	}

	public float readFixed() {
		float retval = readUnsignedShort();
		retval += (readUnsignedShort() / 65536);
		return retval;
	}

	public String readString(int length) {
		return readString(length, "ISO-8859-1");
	}

	public String readString(int length, String encoding) {
		byte[] buffer = readBytes(length);
		try {
			return new String(buffer, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unsupported encoding: " + encoding);
		}
	}

	public final boolean readBoolean() {
		int ch = readUnsignedByte();
		return (ch != 0);
	}

	public final byte readByte() {
		return byteBuffer.get();
	}

	public final short readUnsignedByte() {
		return (short) (byteBuffer.get() & 0xff);
	}
	
	public int readUnsignedInt24() {
		long byte1 = readUnsignedByte();
		long byte2 = readUnsignedByte();
		long byte3 = readUnsignedByte();
		return (int) ((byte1 << 16) + (byte2 << 8) + (byte3 << 0));
	}
	
	public long readUnsignedInt() {
		long byte1 = readUnsignedByte();
		long byte2 = readUnsignedByte();
		long byte3 = readUnsignedByte();
		long byte4 = readUnsignedByte();
		return (byte1 << 24) + (byte2 << 16) + (byte3 << 8) + (byte4 << 0);
	}
	
	public int readUnsignedIntAsInt() {
	    long ulong = readUnsignedInt();
	    if ((ulong & 0x80000000) == 0x80000000) {
	      throw new IllegalStateException("Long value too large to fit into an integer.");
	    }
	    return ((int) ulong) & ~0x80000000;
	  }
	
	public short readShort() {
		return byteBuffer.getShort();
	}
	
	public int readUnsignedShort() {
		int ch1 = readUnsignedByte();
		int ch2 = readUnsignedByte();
		return (ch1 << 8) + (ch2 << 0);
	}
	
	public int[] readUnsignedShortArray(int length) {
		int[] array = new int[length];
		for (int i = 0; i < length; i++) {
			array[i] = readUnsignedShort();
		}
		return array;
	}

	public final char readChar() {
		int ch1 = readUnsignedByte();
		int ch2 = readUnsignedByte();
		return (char) ((ch1 << 8) + (ch2 << 0));
	}
	
	public int readInt() {
		return byteBuffer.getInt();
	}

	public long readLong() {
		return byteBuffer.getLong();
	}
	
	public long readUnsignedLong() {
	    return 0xffffffffL & (readUnsignedByte() << 24 | readUnsignedByte() << 16 | readUnsignedByte() << 8 | readUnsignedByte());
	  }

	public final float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	public final double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	public byte[] readBytes(int numberOfBytes) {
		byte[] data = new byte[numberOfBytes];
		read(data, 0, numberOfBytes);
		return data;
	}

	public void read(byte[] dst, int offset, int len) {
		byteBuffer.get(dst, offset, len);
	}

	@Override
	public void dispose() {
		// TODO byteBuffer.
	}
	
	//TODO unused
	public static class ByteBufferInputStream extends InputStream {
		private ByteBuffer byteBuffer;
		
		public ByteBufferInputStream(ByteBuffer byteBuffer) {
			this.byteBuffer = byteBuffer;
		}

		@Override
		public int read() throws IOException {
			if(byteBuffer.hasRemaining()) {
				return (short) (byteBuffer.get() & 0xff);
			} else {
				return -1;
			}
		}
	}
	
	public static class ByteBufferOutputStream extends OutputStream {
		private ByteBuffer byteBuffer;
		
		public ByteBufferOutputStream(ByteBuffer byteBuffer) {
			this.byteBuffer = byteBuffer;
		}

		@Override
		public void write(int b) throws IOException {
			byteBuffer.put((byte) b);
		}

		public byte[] toByteArray() {
			return byteBuffer.array();
		}
	}
}
