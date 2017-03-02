package com.gurella.engine.utils.struct;

import com.badlogic.gdx.utils.reflect.Field;
import com.gurella.engine.utils.Reflection;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeBuffer extends Buffer {
	private static final Unsafe unsafe;
	private static final long baseOffset;
	static {
		Field field = Reflection.getDeclaredField(Unsafe.class, "theUnsafe");
		field.setAccessible(true);
		unsafe = Reflection.getFieldValue(field, null);
		baseOffset = Unsafe.ARRAY_FLOAT_BASE_OFFSET;
	}

	public UnsafeBuffer(int byteCapacity) {
		super(byteCapacity);
	}

	private static long offset(long offset) {
		return baseOffset + offset;
	}

	@Override
	public void set(Buffer source) {
		unsafe.copyMemory(source.arr, baseOffset, arr, baseOffset, arr.length << 2);
	}

	@Override
	public void set(Buffer source, int sourceOffset, int destinationOffset, int byteLength) {
		unsafe.copyMemory(source.arr, offset(sourceOffset), arr, offset(destinationOffset), byteLength);
	}

	@Override
	public void move(int sourceOffset, int destOffset, int byteLength) {
		unsafe.copyMemory(arr, offset(sourceOffset), arr, offset(destOffset), byteLength);
	}

	@Override
	public float getFloat(int offset) {
		return unsafe.getFloat(arr, offset(offset));
	}

	@Override
	public void setFloat(int offset, float value) {
		unsafe.putFloat(arr, offset(offset), value);
	}

	@Override
	public int getInt(int offset) {
		return unsafe.getInt(arr, offset(offset));
	}

	@Override
	public void setInt(int offset, int value) {
		unsafe.putInt(arr, offset(offset), value);
	}

	@Override
	public long getLong(int offset) {
		return unsafe.getLong(arr, offset(offset));
	}

	@Override
	public void setLong(int offset, long value) {
		unsafe.putLong(arr, offset(offset), value);
	}

	@Override
	public double getDouble(int offset) {
		return unsafe.getDouble(arr, offset(offset));
	}

	@Override
	public void setDouble(int offset, double value) {
		unsafe.putDouble(arr, offset(offset), value);
	}

	@Override
	public short getShort(int offset) {
		return unsafe.getShort(arr, offset(offset));
	}

	@Override
	public void setShort(int offset, short value) {
		unsafe.putShort(arr, offset(offset), value);
	}

	@Override
	public char getChar(int offset) {
		return unsafe.getChar(arr, offset(offset));
	}

	@Override
	public void setChar(int offset, char value) {
		unsafe.putChar(arr, offset(offset), value);
	}

	@Override
	public byte getByte(int offset) {
		return unsafe.getByte(arr, offset(offset));
	}

	@Override
	public void setByte(int offset, byte value) {
		unsafe.putByte(arr, offset(offset), value);
	}

	@Override
	public float[] getFloatArray(int offset, float[] destination) {
		unsafe.copyMemory(arr, offset(offset), destination, baseOffset, destination.length << 2);
		return destination;
	}

	@Override
	public float[] getFloatArray(int offset, float[] destination, int destinationIndex, int count) {
		unsafe.copyMemory(arr, offset(offset), destination, offset(destinationIndex << 2), count << 2);
		return destination;
	}

	@Override
	public void setFloatArray(int offset, float[] source) {
		unsafe.copyMemory(source, baseOffset, arr, offset(offset), source.length << 2);
	}

	@Override
	public void setFloatArray(int offset, float[] source, int count) {
		unsafe.copyMemory(source, baseOffset, arr, offset(offset), count << 2);
	}

	@Override
	public void setFloatArray(int offset, float[] source, int sourceIndex, int count) {
		unsafe.copyMemory(source, offset(sourceIndex << 2), arr, offset(offset), count << 2);
	}
}
