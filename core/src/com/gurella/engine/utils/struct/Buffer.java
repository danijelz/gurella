package com.gurella.engine.utils.struct;

public abstract class Buffer {
	public float[] arr;

	public Buffer(int byteCapacity) {
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

	public void swap(int fromOffset, int toOffset, int byteLength) {
		float[] buffer = this.arr;
		int fromIndex = fromOffset / 4;
		int toIndex = toOffset / 4;
		float temp;
		for (int i = 0, n = byteLength / 4; i < n; i++) {
			temp = buffer[fromIndex];
			buffer[fromIndex++] = buffer[toIndex];
			buffer[toIndex++] = temp;
		}
	}

	public void swap(int fromOffset, int toOffset, float[] temp) {
		float[] buffer = this.arr;
		int length = temp.length;
		int fromIndex = fromOffset / 4;
		int toIndex = toOffset / 4;

		System.arraycopy(buffer, toIndex, temp, 0, length);
		System.arraycopy(buffer, fromIndex, buffer, toIndex, length);
		System.arraycopy(temp, 0, buffer, fromIndex, length);
	}

	public void move(int sourceOffset, int destOffset, int byteLength) {
		System.arraycopy(arr, sourceOffset / 4, arr, destOffset / 4, byteLength / 4);
	}

	/////////// buffer

	public void set(Buffer source) {
		int length = arr.length;
		int otherLength = source.arr.length;
		ensureCapacity((otherLength - length) * 4);
		System.arraycopy(source.arr, 0, arr, 0, otherLength);
	}

	public void set(Buffer source, int sourceOffset, int destinationOffset, int byteLength) {
		int sourceIndex = sourceOffset / 4;
		int destinationIndex = destinationOffset / 4;
		int length = byteLength / 4;
		int neededLength = destinationIndex + length;
		ensureCapacity((neededLength - arr.length) * 4);
		System.arraycopy(source.arr, sourceIndex, arr, destinationOffset / 4, length);
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

	public float[] getFloatArray(int offset, float[] destination, int destinationIndex, int floatLength) {
		System.arraycopy(arr, offset / 4, destination, destinationIndex, floatLength);
		return destination;
	}

	public void setFloatArray(int offset, float[] source) {
		System.arraycopy(source, 0, arr, offset / 4, source.length);
	}

	public void setFloatArray(int offset, float[] source, int floatLength) {
		System.arraycopy(source, 0, arr, offset / 4, floatLength);
	}

	public void setFloatArray(int offset, float[] source, int sourceIndex, int floatLength) {
		System.arraycopy(source, sourceIndex, arr, offset / 4, floatLength);
	}
}
