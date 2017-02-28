package com.gurella.engine.utils.struct;

import com.badlogic.gdx.utils.reflect.Field;
import com.gurella.engine.utils.Reflection;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeBuffer extends BaseBuffer {
	final static Unsafe unsafe;
	static {
		Field field = Reflection.getDeclaredField(Unsafe.class, "theUnsafe");
		field.setAccessible(true);
		unsafe = Reflection.getFieldValue(field, null);
	}

	public UnsafeBuffer(int byteCapacity) {
		super(byteCapacity);
	}

	private static long unsafeOffset(long offset) {
		return Unsafe.ARRAY_FLOAT_BASE_OFFSET + offset;
	}

	@Override
	public void move(int sourceOffset, int destOffset, int byteLength) {
		unsafe.copyMemory(arr, unsafeOffset(sourceOffset), arr, unsafeOffset(destOffset), byteLength);
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
	public float[] getFloatArray(int offset, float[] destination) {
		unsafe.copyMemory(arr, unsafeOffset(offset), destination, unsafeOffset(0), destination.length * 4);
		return destination;
	}

	@Override
	public float[] getFloatArray(int offset, float[] destination, int destinationOffset, int floatLength) {
		unsafe.copyMemory(arr, unsafeOffset(offset), destination, unsafeOffset(destinationOffset), floatLength * 4);
		return destination;
	}

	@Override
	public void setFloatArray(float[] source, int destOffset) {
		unsafe.copyMemory(source, unsafeOffset(0), arr, unsafeOffset(destOffset), source.length * 4);
	}

	@Override
	public void setFloatArray(float[] source, int destOffset, int floatLength) {
		unsafe.copyMemory(source, unsafeOffset(0), arr, unsafeOffset(destOffset), floatLength * 4);
	}

	@Override
	public void setFloatArray(float[] source, int sourceOffset, int destOffset, int floatLength) {
		unsafe.copyMemory(source, unsafeOffset(sourceOffset), arr, unsafeOffset(destOffset), floatLength * 4);
	}
}
