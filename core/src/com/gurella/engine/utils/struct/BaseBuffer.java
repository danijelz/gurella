package com.gurella.engine.utils.struct;

public abstract class BaseBuffer {
	public float[] arr;

	public BaseBuffer(int byteCapacity) {
		this.arr = new float[byteCapacity / 4];
	}

	public int getCapacity() {
		return arr.length * 4;
	}

	public void ensureCapacity(int additionalByteCapacity) {
		int bufferSize = arr.length;
		int newBufferSize = bufferSize + additionalByteCapacity / 4;
		if (newBufferSize > bufferSize) {
			_resize(Math.max(8, newBufferSize));
		}
	}

	public void resize(int newByteCapacity) {
		_resize(newByteCapacity / 4);
	}

	private void _resize(int newCapacity) {
		float[] buffer = this.arr;
		float[] newBuffer = new float[newCapacity];
		System.arraycopy(buffer, 0, newBuffer, 0, Math.min(buffer.length, newCapacity));
		this.arr = newBuffer;
	}

	public void swap(int firstIndex, int secondIndex, int byteLength) {
		float[] buffer = this.arr;
		int firstOffset = firstIndex / 4;
		int secondOffset = secondIndex / 4;
		float word;
		for (int i = 0, n = byteLength / 4; i < n; i++) {
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
		setFloatArray(temp, secondOffset, length);
	}

	public void move(int sourceOffset, int destOffset, int byteLength) {
		System.arraycopy(arr, sourceOffset / 4, arr, destOffset / 4, byteLength / 4);
	}

	/////////// buffer

	public void set(BaseBuffer other) {
		int otherLength = other.arr.length;
		int length = arr.length;
		ensureCapacity(otherLength - length);
		System.arraycopy(arr, 0, other.arr, 0, Math.min(otherLength, length));
	}

	public void set(BaseBuffer source, int sourceByteOffset, int destinationByteOffset, int byteLength) {
		setFloatArray(source.arr, sourceByteOffset, destinationByteOffset, byteLength);
	}

	/////////// float

	public abstract float getFloat(int offset);

	public abstract void setFloat(int offset, float value);

	/////////// int

	public abstract int getInt(int offset);

	public abstract void setInt(int offset, int value);

	////////// long

	public abstract long getLong(int offset);

	public abstract void setLong(int offset, long value);

	///////// double

	public abstract double getDouble(int offset);

	public abstract void setDouble(int offset, double value);

	///////// short

	public abstract short getShort(int offset);

	public abstract void setShort(int offset, short value);

	///////// char

	public abstract char getChar(int offset);

	public abstract void setChar(int offset, char value);

	//////// byte

	public abstract byte getByte(int offset);

	public abstract void setByte(int offset, byte value);

	//////// flag

	public boolean getFlag(int offset, int flag) {
		return (getInt(offset) & (1 << flag)) != 0;
	}

	public void setFlag(int offset, int flag) {
		setInt(offset, getInt(offset) | (1 << flag));
	}

	public void unsetFlag(int offset, int flag) {
		setInt(offset, getInt(offset) & ~(1 << flag));
	}

	//////// float[]

	public float[] getFloatArray(int offset, float[] destination) {
		System.arraycopy(arr, offset / 4, destination, 0, destination.length);
		return destination;
	}

	public float[] getFloatArray(int offset, float[] destination, int destinationOffset, int floatLength) {
		System.arraycopy(arr, offset / 4, destination, destinationOffset, floatLength);
		return destination;
	}

	public void setFloatArray(float[] source, int destOffset) {
		System.arraycopy(source, 0, arr, destOffset / 4, source.length);
	}

	public void setFloatArray(float[] source, int destOffset, int floatLength) {
		System.arraycopy(source, 0, arr, destOffset / 4, floatLength);
	}

	public void setFloatArray(float[] source, int sourceOffset, int destOffset, int floatLength) {
		System.arraycopy(source, sourceOffset / 4, arr, destOffset / 4, floatLength);
	}
}
