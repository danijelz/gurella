package com.gurella.engine.utils.struct;

import com.badlogic.gdx.utils.reflect.Field;
import com.gurella.engine.utils.Reflection;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeBuffer extends Buffer {
	final static Unsafe unsafe;
	static {
		Field field = Reflection.getDeclaredField(Unsafe.class, "theUnsafe");
		field.setAccessible(true);
		unsafe = Reflection.getFieldValue(field, null);
	}

	public UnsafeBuffer(int byteCapacity) {
		super(byteCapacity);
	}

	private static long arrOffset(long offset) {
		return Unsafe.ARRAY_FLOAT_BASE_OFFSET + offset;
	}

	@Override
	public void set(Buffer source) {
		int length = arr.length;
		int otherLength = source.arr.length;
		ensureCapacity((otherLength - length) * 4);
		unsafe.copyMemory(source.arr, arrOffset(0), arr, arrOffset(0), length * 4);
	}

	@Override
	public void set(Buffer source, int sourceOffset, int destinationOffset, int byteLength) {
		int destinationIndex = destinationOffset / 4;
		int length = byteLength / 4;
		int neededLength = destinationIndex + length;
		ensureCapacity((neededLength - arr.length) * 4);
		unsafe.copyMemory(source.arr, arrOffset(sourceOffset), arr, arrOffset(destinationOffset), byteLength);
	}

	@Override
	public void move(int sourceOffset, int destOffset, int byteLength) {
		unsafe.copyMemory(arr, arrOffset(sourceOffset), arr, arrOffset(destOffset), byteLength);
	}

	@Override
	public float getFloat(int offset) {
		return unsafe.getFloat(arr, arrOffset(offset));
	}

	@Override
	public void setFloat(int offset, float value) {
		unsafe.putFloat(arr, arrOffset(offset), value);
	}

	@Override
	public int getInt(int offset) {
		return unsafe.getInt(arr, arrOffset(offset));
	}

	@Override
	public void setInt(int offset, int value) {
		unsafe.putInt(arr, arrOffset(offset), value);
	}

	@Override
	public long getLong(int offset) {
		return unsafe.getLong(arr, arrOffset(offset));
	}

	@Override
	public void setLong(int offset, long value) {
		unsafe.putLong(arr, arrOffset(offset), value);
	}

	@Override
	public double getDouble(int offset) {
		return unsafe.getDouble(arr, arrOffset(offset));
	}

	@Override
	public void setDouble(int offset, double value) {
		unsafe.putDouble(arr, arrOffset(offset), value);
	}

	@Override
	public short getShort(int offset) {
		return unsafe.getShort(arr, arrOffset(offset));
	}

	@Override
	public void setShort(int offset, short value) {
		unsafe.putShort(arr, arrOffset(offset), value);
	}

	@Override
	public char getChar(int offset) {
		return unsafe.getChar(arr, arrOffset(offset));
	}

	@Override
	public void setChar(int offset, char value) {
		unsafe.putChar(arr, arrOffset(offset), value);
	}

	@Override
	public byte getByte(int offset) {
		return unsafe.getByte(arr, arrOffset(offset));
	}

	@Override
	public void setByte(int offset, byte value) {
		unsafe.putByte(arr, arrOffset(offset), value);
	}

	@Override
	public float[] getFloatArray(int offset, float[] destination) {
		unsafe.copyMemory(arr, arrOffset(offset), destination, arrOffset(0), destination.length * 4);
		return destination;
	}

	@Override
	public float[] getFloatArray(int offset, float[] destination, int destinationIndex, int floatLength) {
		unsafe.copyMemory(arr, arrOffset(offset), destination, arrOffset(destinationIndex * 4), floatLength * 4);
		return destination;
	}

	@Override
	public void setFloatArray(int offset, float[] source) {
		unsafe.copyMemory(source, arrOffset(0), arr, arrOffset(offset), source.length * 4);
	}

	@Override
	public void setFloatArray(int offset, float[] source, int floatLength) {
		unsafe.copyMemory(source, arrOffset(0), arr, arrOffset(offset), floatLength * 4);
	}

	@Override
	public void setFloatArray(int offset, float[] source, int sourceIndex, int floatLength) {
		unsafe.copyMemory(source, arrOffset(sourceIndex * 4), arr, arrOffset(offset), floatLength * 4);
	}
}
