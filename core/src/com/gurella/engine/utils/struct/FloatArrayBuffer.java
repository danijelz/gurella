package com.gurella.engine.utils.struct;

import static com.badlogic.gdx.utils.NumberUtils.floatToRawIntBits;
import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.longBitsToDouble;
import static java.lang.Float.intBitsToFloat;

public class FloatArrayBuffer extends Buffer {
	public FloatArrayBuffer(int byteCapacity) {
		super(byteCapacity);
	}

	//////// float

	@Override
	public float getFloat(int offset) {
		return arr[offset / 4];
	}

	@Override
	public void setFloat(int offset, float value) {
		arr[offset / 4] = value;
	}

	/////////// int

	@Override
	public int getInt(int offset) {
		return floatToRawIntBits(arr[offset / 4]);
	}

	@Override
	public void setInt(int offset, int value) {
		arr[offset / 4] = intBitsToFloat(value);
	}

	////////// long

	@Override
	public long getLong(int offset) {
		int temp = offset / 4;
		float[] buffer = this.arr;
		return (long) floatToRawIntBits(buffer[temp++]) << 32 | floatToRawIntBits(buffer[temp]) & 0xFFFFFFFFL;
	}

	@Override
	public void setLong(int offset, long value) {
		int temp = offset / 4;
		float[] buffer = this.arr;
		buffer[temp++] = intBitsToFloat((int) (value >> 32));
		buffer[temp] = intBitsToFloat((int) value);
	}

	///////// double

	@Override
	public double getDouble(int offset) {
		float[] buffer = this.arr;
		int temp = offset / 4;
		long hi = (long) floatToRawIntBits(buffer[temp++]) << 32;
		long lo = floatToRawIntBits(buffer[temp]) & 0xFFFFFFFFL;
		return longBitsToDouble(hi | lo);
	}

	@Override
	public void setDouble(int offset, double value) {
		long l = doubleToRawLongBits(value);
		float[] buffer = this.arr;
		int temp = offset / 4;
		buffer[temp++] = intBitsToFloat((int) (l >> 32));
		buffer[temp] = intBitsToFloat((int) l);
	}

	///////// short

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
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
}
