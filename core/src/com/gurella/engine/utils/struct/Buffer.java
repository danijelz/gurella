package com.gurella.engine.utils.struct;

import static com.badlogic.gdx.utils.NumberUtils.floatToRawIntBits;
import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.longBitsToDouble;
import static java.lang.Float.intBitsToFloat;

import java.util.Arrays;

public class Buffer {
	public float[] arr;

	public Buffer(int capacity) {
		this.arr = new float[capacity];
	}

	public void ensureCapacity(int additionalCapacity) {
		int bufferSize = arr.length;
		int newBufferSize = bufferSize + additionalCapacity;
		if (newBufferSize > bufferSize) {
			resize(Math.max(8, newBufferSize));
		}
	}

	public void resize(int newBufferSize) {
		float[] buffer = this.arr;
		float[] newBuffer = new float[newBufferSize];
		System.arraycopy(buffer, 0, newBuffer, 0, Math.min(buffer.length, newBufferSize));
		this.arr = newBuffer;
	}

	public void swap(int firstIndex, int secondIndex, int count) {
		float[] buffer = this.arr;
		int firstOffset = firstIndex;
		int secondOffset = secondIndex;
		float word;
		for (int i = 0; i < count; i++) {
			word = buffer[firstOffset];
			buffer[firstOffset++] = buffer[secondOffset];
			buffer[secondOffset++] = word;
		}
	}

	public void swap(int firstIndex, int secondIndex, float[] temp) {
		float[] buffer = this.arr;
		int firstOffset = firstIndex;
		int secondOffset = secondIndex;
		int length = temp.length;
		getFloatArray(firstOffset, temp, 0, length);
		System.arraycopy(buffer, secondOffset, buffer, firstOffset, length);
		setFloatArray(secondOffset, temp, length);
	}

	public void fill(int offset, int count, float val) {
		Arrays.fill(arr, offset, offset + count, val);
	}

	public void move(int sourceOffset, int destOffset, int count) {
		System.arraycopy(arr, sourceOffset, arr, destOffset, count);
	}

	public void set(Buffer other) {
		int otherLength = other.arr.length;
		int length = arr.length;
		ensureCapacity(otherLength - length);
		System.arraycopy(arr, 0, other.arr, 0, Math.min(otherLength, length));
	}

	//////// float

	public float getFloat(int offset) {
		return arr[offset];
	}

	public void setFloat(int offset, float value) {
		arr[offset] = value;
	}

	//////// float[]

	public float[] getFloatArray(int offset, float[] destination) {
		System.arraycopy(arr, offset, destination, 0, destination.length);
		return destination;
	}

	public float[] getFloatArray(int offset, float[] destination, int destinationOffset, int length) {
		System.arraycopy(arr, offset, destination, destinationOffset, length);
		return destination;
	}

	public void setFloatArray(int offset, float[] source) {
		System.arraycopy(source, 0, arr, offset, source.length);
	}

	public void setFloatArray(int offset, float[] source, int length) {
		System.arraycopy(source, 0, arr, offset, length);
	}

	public void setFloatArray(float[] source, int sourceOffset, int offset, int length) {
		System.arraycopy(source, sourceOffset, arr, offset, length);
	}

	/////////// int

	public int getInt(int offset) {
		return floatToRawIntBits(arr[offset]);
	}

	public void setInt(int offset, int value) {
		arr[offset] = intBitsToFloat(value);
	}

	////////// long

	public long getLong(int offset) {
		int temp = offset;
		float[] buffer = this.arr;
		return (long) floatToRawIntBits(buffer[temp++]) << 32 | floatToRawIntBits(buffer[temp]) & 0xFFFFFFFFL;
	}

	public void setLong(int offset, long value) {
		int temp = offset;
		float[] buffer = this.arr;
		buffer[temp++] = intBitsToFloat((int) (value >> 32));
		buffer[temp] = intBitsToFloat((int) value);
	}

	///////// double

	public double getDouble(int offset) {
		float[] buffer = this.arr;
		long hi = (long) floatToRawIntBits(buffer[offset]) << 32;
		long lo = floatToRawIntBits(buffer[offset + 1]) & 0xFFFFFFFFL;
		return longBitsToDouble(hi | lo);
	}

	public void setDouble(int offset, double value) {
		long l = doubleToRawLongBits(value);
		float[] buffer = this.arr;
		buffer[offset] = intBitsToFloat((int) (l >> 32));
		buffer[offset + 1] = intBitsToFloat((int) l);
	}

	///////// short

	public short getShort(int offset) {
		return (short) floatToRawIntBits(arr[offset]);
	}

	public void setShort(int offset, short value) {
		arr[offset] = intBitsToFloat(value);
	}

	public short getShort0(int offset) {
		return (short) (floatToRawIntBits(arr[offset]) >> 16);
	}

	public void setShort0(int offset, short value) {
		float[] buffer = this.arr;
		int i = floatToRawIntBits(buffer[offset]) & 0x0000FFFF;
		buffer[offset] = intBitsToFloat(i | (value << 16));
	}

	public short getShort2(int offset) {
		return (short) floatToRawIntBits(arr[offset]);
	}

	public void setShort2(int offset, short value) {
		float[] buffer = this.arr;
		int i = floatToRawIntBits(buffer[offset]) & 0xFFFF0000;
		buffer[offset] = intBitsToFloat(i | value);
	}

	///////// char

	public char getChar(int offset) {
		return (char) floatToRawIntBits(arr[offset]);
	}

	public void setChar(int offset, char value) {
		arr[offset] = intBitsToFloat(value);
	}

	public char getChar0(int offset) {
		return (char) (floatToRawIntBits(arr[offset]) >> 16);
	}

	public void setChar0(int offset, char value) {
		float[] buffer = this.arr;
		int i = floatToRawIntBits(buffer[offset]) & 0x0000FFFF;
		buffer[offset] = intBitsToFloat(i | (value << 16));
	}

	public char getChar2(int offset) {
		return (char) floatToRawIntBits(arr[offset]);
	}

	public void setChar2(int offset, char value) {
		float[] buffer = this.arr;
		int i = floatToRawIntBits(buffer[offset]) & 0xFFFF0000;
		buffer[offset] = intBitsToFloat(i | value);
	}

	//////// byte

	public byte getByte(int offset) {
		return (byte) floatToRawIntBits(arr[offset]);
	}

	public void setByte(int offset, byte value) {
		arr[offset] = intBitsToFloat(value);
	}

	public byte getByte0(int offset) {
		return (byte) (floatToRawIntBits(arr[offset]) >> 24);
	}

	public void setByte0(int offset, byte value) {
		float[] buffer = this.arr;
		int i = floatToRawIntBits(buffer[offset]) & 0x00FFFFFF;
		buffer[offset] = intBitsToFloat(i | (value << 24));
	}

	public byte getByte1(int offset) {
		return (byte) (floatToRawIntBits(arr[offset]) >> 24);
	}

	public void setByte1(int offset, byte value) {
		float[] buffer = this.arr;
		int i = floatToRawIntBits(buffer[offset]) & 0xFF00FFFF;
		buffer[offset] = intBitsToFloat(i | (value << 16));
	}

	public byte getByte2(int offset) {
		return (byte) (floatToRawIntBits(arr[offset]) >> 16);
	}

	public void setByte2(int offset, byte value) {
		float[] buffer = this.arr;
		int i = floatToRawIntBits(buffer[offset]) & 0xFFFF00FF;
		buffer[offset] = intBitsToFloat(i | (value << 8));
	}

	public byte getByte3(int offset) {
		return (byte) (floatToRawIntBits(arr[offset]) >> 8);
	}

	public void setByte3(int offset, byte value) {
		float[] buffer = this.arr;
		int i = floatToRawIntBits(buffer[offset]) & 0xFFFFFF00;
		buffer[offset] = intBitsToFloat(i | value);
	}

	//////// flag

	public boolean getFlag(int offset, int flag) {
		return (floatToRawIntBits(arr[offset]) & (1 << flag)) != 0;
	}

	public void setFlag(int offset, int flag) {
		float[] buffer = this.arr;
		int value = floatToRawIntBits(buffer[offset]);
		buffer[offset] = intBitsToFloat(value | (1 << flag));
	}

	public void unsetFlag(int offset, int flag) {
		float[] buffer = this.arr;
		int value = floatToRawIntBits(buffer[offset]);
		buffer[offset] = intBitsToFloat(value & ~(1 << flag));
	}
}
