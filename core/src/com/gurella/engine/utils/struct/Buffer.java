package com.gurella.engine.utils.struct;

import static com.badlogic.gdx.utils.NumberUtils.floatToRawIntBits;
import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.longBitsToDouble;
import static java.lang.Float.intBitsToFloat;

import java.util.Arrays;

public class Buffer {
	public float[] arr;

	public Buffer(int capacity) {
		this.arr = new float[capacity / 4];
	}

	public int getCapacity() {
		return arr.length * 4;
	}

	public void ensureCapacity(int additionalCapacity) {
		int bufferSize = arr.length;
		int newBufferSize = bufferSize + additionalCapacity / 4;
		if (newBufferSize > bufferSize) {
			_resize(Math.max(8, newBufferSize));
		}
	}

	public void resize(int newBufferSize) {
		_resize(newBufferSize / 4);
	}

	private void _resize(int newBufferSize) {
		float[] buffer = this.arr;
		float[] newBuffer = new float[newBufferSize];
		System.arraycopy(buffer, 0, newBuffer, 0, Math.min(buffer.length, newBufferSize));
		this.arr = newBuffer;
	}

	public void swap(int firstIndex, int secondIndex, int count) {
		float[] buffer = this.arr;
		int firstOffset = firstIndex / 4;
		int secondOffset = secondIndex / 4;
		float word;
		for (int i = 0; i < count; i++) {
			word = buffer[firstOffset];
			buffer[firstOffset++] = buffer[secondOffset];
			buffer[secondOffset++] = word;
		}
	}

	public void swap(int firstIndex, int secondIndex, float[] temp) {
		float[] buffer = this.arr;
		int firstOffset = firstIndex / 4;
		int secondOffset = secondIndex / 4;
		int length = temp.length;
		getFloatArray(firstOffset, temp, 0, length);
		System.arraycopy(buffer, secondOffset, buffer, firstOffset, length);
		setFloatArray(secondOffset, temp, length);
	}

	public void fill(int offset, int count, float val) {
		int temp = offset / 4;
		Arrays.fill(arr, temp, temp + count, val);
	}

	public void move(int sourceOffset, int destOffset, int count) {
		System.arraycopy(arr, sourceOffset / 4, arr, destOffset / 4, count / 4);
	}

	public void set(Buffer other) {
		int otherLength = other.arr.length;
		int length = arr.length;
		ensureCapacity(otherLength - length);
		System.arraycopy(arr, 0, other.arr, 0, Math.min(otherLength, length));
	}

	//////// float

	public float getFloat(int offset) {
		return arr[offset / 4];
	}

	public void setFloat(int offset, float value) {
		arr[offset / 4] = value;
	}

	//////// float[]

	public float[] getFloatArray(int offset, float[] destination) {
		System.arraycopy(arr, offset / 4, destination, 0, destination.length);
		return destination;
	}

	public float[] getFloatArray(int offset, float[] destination, int destOffset, int length) {
		System.arraycopy(arr, offset / 4, destination, destOffset / 4, length);
		return destination;
	}

	public void setFloatArray(int offset, float[] source) {
		System.arraycopy(source, 0, arr, offset / 4, source.length);
	}

	public void setFloatArray(int offset, float[] source, int length) {
		System.arraycopy(source, 0, arr, offset / 4, length);
	}

	public void setFloatArray(float[] source, int sourceOffset, int destOffset, int length) {
		System.arraycopy(source, sourceOffset / 4, arr, destOffset / 4, length);
	}

	/////////// int

	public int getInt(int offset) {
		return floatToRawIntBits(arr[offset / 4]);
	}

	public void setInt(int offset, int value) {
		arr[offset / 4] = intBitsToFloat(value);
	}

	////////// long

	public long getLong(int offset) {
		int temp = offset / 4;
		float[] buffer = this.arr;
		return (long) floatToRawIntBits(buffer[temp++]) << 32 | floatToRawIntBits(buffer[temp]) & 0xFFFFFFFFL;
	}

	public void setLong(int offset, long value) {
		int temp = offset / 4;
		float[] buffer = this.arr;
		buffer[temp++] = intBitsToFloat((int) (value >> 32));
		buffer[temp] = intBitsToFloat((int) value);
	}

	///////// double

	public double getDouble(int offset) {
		float[] buffer = this.arr;
		int temp = offset / 4;
		long hi = (long) floatToRawIntBits(buffer[temp++]) << 32;
		long lo = floatToRawIntBits(buffer[temp]) & 0xFFFFFFFFL;
		return longBitsToDouble(hi | lo);
	}

	public void setDouble(int offset, double value) {
		long l = doubleToRawLongBits(value);
		float[] buffer = this.arr;
		int temp = offset / 4;
		buffer[temp++] = intBitsToFloat((int) (l >> 32));
		buffer[temp] = intBitsToFloat((int) l);
	}

	///////// short

	public short getShort(int offset) {
		int temp = offset / 4;
		float[] buffer = this.arr;

		switch (offset % 4) {
		case 0:
			return (short) (floatToRawIntBits(buffer[temp]) >> 16);
		case 2:
			return (short) floatToRawIntBits(buffer[temp]);
		default:
			throw new IllegalArgumentException("Invalid short offset: " + offset);
		}
	}

	public void setShort(int offset, short value) {
		int temp = offset / 4;
		float[] buffer = this.arr;

		switch (offset % 4) {
		case 0: {
			int i = floatToRawIntBits(buffer[temp]) & 0x0000FFFF;
			buffer[temp] = intBitsToFloat(i | (value << 16));
			break;
		}
		case 2: {
			int i = floatToRawIntBits(buffer[temp]) & 0xFFFF0000;
			buffer[temp] = intBitsToFloat(i | value);
			break;
		}
		default:
			throw new IllegalArgumentException("Invalid short offset: " + offset);
		}
	}

	///////// char

	public char getChar(int offset) {
		int temp = offset / 4;
		float[] buffer = this.arr;

		switch (offset % 4) {
		case 0:
			return (char) (floatToRawIntBits(buffer[temp]) >> 16);
		case 2:
			return (char) floatToRawIntBits(buffer[temp]);
		default:
			throw new IllegalArgumentException("Invalid char offset: " + offset);
		}
	}

	public void setChar(int offset, char value) {
		int temp = offset / 4;
		float[] buffer = this.arr;

		switch (offset % 4) {
		case 0: {
			int i = floatToRawIntBits(buffer[temp]) & 0x0000FFFF;
			buffer[temp] = intBitsToFloat(i | (value << 16));
			break;
		}
		case 2: {
			int i = floatToRawIntBits(buffer[temp]) & 0xFFFF0000;
			buffer[temp] = intBitsToFloat(i | value);
			break;
		}
		default:
			throw new IllegalArgumentException("Invalid short offset: " + offset);
		}
	}

	//////// byte

	public byte getByte(int offset) {
		int temp = offset / 4;
		float[] buffer = this.arr;

		switch (offset % 4) {
		case 0:
			return (byte) (floatToRawIntBits(buffer[temp]) >> 24);
		case 1:
			return (byte) (floatToRawIntBits(buffer[temp]) >> 16);
		case 2:
			return (byte) (floatToRawIntBits(buffer[temp]) >> 8);
		case 3:
			return (byte) (floatToRawIntBits(buffer[temp]));
		default:
			throw new IllegalArgumentException("Invalid char offset: " + offset);
		}
	}

	public void setByte(int offset, byte value) {
		int temp = offset / 4;
		float[] buffer = this.arr;

		switch (offset % 4) {
		case 0: {
			int i = floatToRawIntBits(buffer[temp]) & 0x00FFFFFF;
			buffer[temp] = intBitsToFloat(i | (value << 24));
			break;
		}
		case 1: {
			int i = floatToRawIntBits(buffer[temp]) & 0xFF00FFFF;
			buffer[temp] = intBitsToFloat(i | (value << 16));
			break;
		}
		case 2: {
			int i = floatToRawIntBits(buffer[temp]) & 0xFFFF00FF;
			buffer[temp] = intBitsToFloat(i | (value << 8));
			break;
		}
		case 3: {
			int i = floatToRawIntBits(buffer[temp]) & 0xFFFFFF00;
			buffer[temp] = intBitsToFloat(i | value);
			break;
		}
		default:
			throw new IllegalArgumentException("Invalid short offset: " + offset);
		}
	}

	//////// flag

	public boolean getFlag(int offset, int flag) {
		return (floatToRawIntBits(arr[offset / 4]) & (1 << flag)) != 0;
	}

	public void setFlag(int offset, int flag) {
		float[] buffer = this.arr;
		int temp = offset / 4;
		int value = floatToRawIntBits(buffer[temp]);
		buffer[temp] = intBitsToFloat(value | (1 << flag));
	}

	public void unsetFlag(int offset, int flag) {
		float[] buffer = this.arr;
		int temp = offset / 4;
		int value = floatToRawIntBits(buffer[temp]);
		buffer[temp] = intBitsToFloat(value & ~(1 << flag));
	}
}
