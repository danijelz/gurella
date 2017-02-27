package com.gurella.engine.utils.struct;

import com.badlogic.gdx.utils.reflect.Field;
import com.gurella.engine.utils.Reflection;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeBuffer implements IBuffer {
	final static Unsafe unsafe;
	static {
		Field field = Reflection.getDeclaredField(Unsafe.class, "theUnsafe");
		field.setAccessible(true);
		unsafe = Reflection.getFieldValue(field, null);
	}

	private byte[] arr;

	public UnsafeBuffer(int capacity) {
		this.arr = new byte[capacity];
	}

	@Override
	public int getCapacity() {
		return arr.length;
	}

	@Override
	public void ensureCapacity(int additionalCapacity) {
		int bufferSize = arr.length;
		int newBufferSize = bufferSize + additionalCapacity;
		if (newBufferSize > bufferSize) {
			resize(Math.max(8 * 4, newBufferSize));
		}
	}

	@Override
	public void resize(int newBufferSize) {
		byte[] buffer = this.arr;
		byte[] newBuffer = new byte[newBufferSize];
		System.arraycopy(buffer, 0, newBuffer, 0, Math.min(buffer.length, newBufferSize));
		this.arr = newBuffer;
	}

	private static long unsafeOffset(long offset) {
		return Unsafe.ARRAY_BYTE_BASE_OFFSET + offset;
	}

	private static long unsafeOffset() {
		return Unsafe.ARRAY_BYTE_BASE_OFFSET;
	}

	final boolean getBooleanU(long offset) {
		return unsafe.getBoolean(arr, unsafeOffset(offset));
	}

	final void putBooleanU(long offset, boolean b) {
		unsafe.putBoolean(arr, unsafeOffset(offset), b);
	}

	final byte getByteU(long offset) {
		return unsafe.getByte(arr, unsafeOffset(offset));
	}

	final void putByteU(long offset, byte b) {
		unsafe.putByte(arr, unsafeOffset(offset), b);
	}

	final char getCharU(long offset) {
		return unsafe.getChar(arr, unsafeOffset(offset));
	}

	final void putCharU(long offset, char c) {
		unsafe.putChar(arr, unsafeOffset(offset), c);
	}

	final short getShortU(long offset) {
		return unsafe.getShort(arr, unsafeOffset(offset));
	}

	final void putShortU(long offset, short i) {
		unsafe.putShort(arr, unsafeOffset(offset), i);
	}

	final int getIntU(long offset) {
		return unsafe.getInt(arr, unsafeOffset(offset));
	}

	final void putIntU(long offset, int i) {
		unsafe.putInt(arr, unsafeOffset(offset), i);
	}

	final long getLongU(long offset) {
		return unsafe.getLong(arr, unsafeOffset(offset));
	}

	final void putLongU(long offset, long l) {
		unsafe.putLong(arr, unsafeOffset(offset), l);
	}

	final float getFloatU(long offset) {
		return unsafe.getFloat(arr, unsafeOffset(offset));
	}

	final void putFloatU(long offset, float v) {
		unsafe.putFloat(arr, unsafeOffset(offset), v);
	}

	final double getDoubleU(long offset) {
		return unsafe.getDouble(arr, unsafeOffset(offset));
	}

	final void putDoubleU(long offset, double v) {
		unsafe.putDouble(arr, unsafeOffset(offset), v);
	}

	@Override
	public void swap(int firstIndex, int secondIndex, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void swap(int firstIndex, int secondIndex, float[] temp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fill(int offset, int count, float val) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(int sourceOffset, int destOffset, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getFloat(int offset) {
		return unsafe.getFloat(arr, unsafeOffset(offset));
	}

	@Override
	public void setFloat(int offset, float value) {
		unsafe.putFloat(arr, unsafeOffset(offset), value);
	}

	@Override
	public float[] getFloatArray(int offset, float[] destination) {
		// TODO Auto-generated method stub
		return destination;
	}

	@Override
	public float[] getFloatArray(int offset, float[] destination, int destinationOffset, int length) {
		// TODO Auto-generated method stub
		return destination;
	}

	@Override
	public void setFloatArray(int offset, float[] source) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFloatArray(int offset, float[] source, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFloatArray(float[] source, int sourceOffset, int offset, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getInt(int offset) {
		return unsafe.getInt(arr, unsafeOffset(offset));
	}

	@Override
	public void setInt(int offset, int value) {
		unsafe.putInt(arr, unsafeOffset(offset), value);
	}

	@Override
	public long getLong(int offset) {
		return unsafe.getLong(arr, unsafeOffset(offset));
	}

	@Override
	public void setLong(int offset, long value) {
		unsafe.putLong(arr, unsafeOffset(offset), value);
	}

	@Override
	public double getDouble(int offset) {
		return unsafe.getDouble(arr, unsafeOffset(offset));
	}

	@Override
	public void setDouble(int offset, double value) {
		unsafe.putDouble(arr, unsafeOffset(offset), value);
	}

	@Override
	public short getShort(int offset) {
		return unsafe.getShort(arr, unsafeOffset(offset));
	}

	@Override
	public void setShort(int offset, short value) {
		unsafe.putShort(arr, unsafeOffset(offset), value);
	}

	@Override
	public char getChar(int offset) {
		return unsafe.getChar(arr, unsafeOffset(offset));
	}

	@Override
	public void setChar(int offset, char value) {
		unsafe.putChar(arr, unsafeOffset(offset), value);
	}

	@Override
	public byte getByte(int offset) {
		return unsafe.getByte(arr, unsafeOffset(offset));
	}

	@Override
	public void setByte(int offset, byte value) {
		unsafe.putByte(arr, unsafeOffset(offset), value);
	}

	@Override
	public boolean getFlag(int offset, int flag) {
		return (unsafe.getInt(arr, unsafeOffset(offset)) & (1 << flag)) != 0;
	}

	@Override
	public void setFlag(int offset, int flag) {
		int value = getInt(offset);
		setInt(offset, value | (1 << flag));
	}

	@Override
	public void unsetFlag(int offset, int flag) {
		int value = getInt(offset);
		setInt(offset, value & ~(1 << flag));
	}
}
