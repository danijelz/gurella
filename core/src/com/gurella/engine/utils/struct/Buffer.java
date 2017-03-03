package com.gurella.engine.utils.struct;

import static com.badlogic.gdx.utils.NumberUtils.floatToRawIntBits;

import java.util.Arrays;

public abstract class Buffer {
	public static final int word = 4;

	public float[] arr;

	public Buffer(int byteCapacity) {
		this.arr = new float[byteCapacity >> 2];
	}

	public int getCapacity() {
		return arr.length << 2;
	}

	public void ensureCapacity(int additionalByteCapacity) {
		int bufferSize = arr.length;
		int newBufferSize = bufferSize + additionalByteCapacity >> 2;
		if (newBufferSize > bufferSize) {
			_resize(Math.max(8, newBufferSize));
		}
	}

	public void resize(int newByteCapacity) {
		_resize(newByteCapacity >> 2);
	}

	private void _resize(int newWordCapacity) {
		float[] buffer = this.arr;
		float[] newBuffer = new float[newWordCapacity];
		System.arraycopy(buffer, 0, newBuffer, 0, Math.min(buffer.length, newWordCapacity));
		this.arr = newBuffer;
	}

	public void swap(int fromOffset, int toOffset, int byteLength) {
		float[] buffer = this.arr;
		int fromIndex = fromOffset >> 2;
		int toIndex = toOffset >> 2;
		float temp;
		for (int i = 0, n = byteLength >> 2; i < n; i++) {
			temp = buffer[fromIndex];
			buffer[fromIndex++] = buffer[toIndex];
			buffer[toIndex++] = temp;
		}
	}

	public void swap(int fromOffset, int toOffset, int byteLength, float[] tempStorage) {
		float[] buffer = this.arr;
		int storageLength = tempStorage.length;
		int fromIndex = fromOffset >> 2;
		int toIndex = toOffset >> 2;
		int count = byteLength >> 2;

		while (count > 0) {
			int length = Math.min(storageLength, count);
			System.arraycopy(buffer, toIndex, tempStorage, 0, length);
			System.arraycopy(buffer, fromIndex, buffer, toIndex, length);
			System.arraycopy(tempStorage, 0, buffer, fromIndex, length);

			fromIndex += length;
			toIndex += length;
			count -= length;
		}
	}

	public void move(int sourceOffset, int destOffset, int byteLength) {
		System.arraycopy(arr, sourceOffset >> 2, arr, destOffset >> 2, byteLength >> 2);
	}

	public void fill(int fromIndex, int toIndex, float value) {
		Arrays.fill(arr, fromIndex, toIndex, value);
	}

	public boolean equals(int offset, Buffer other, int otherOffset, int byteLength) {
		float[] arr1 = arr;
		float[] arr2 = other.arr;
		int index1 = offset >> 2;
		int index2 = otherOffset >> 2;
		int n = index1 + byteLength >> 2;

		for (; index1 < n; index1++, index2++) {
			if (floatToRawIntBits(arr1[index1]) != floatToRawIntBits(arr2[index2])) {
				return false;
			}
		}

		return true;
	}

	public int hashCode(int offset, int byteLength) {
		float[] arr = this.arr;
		int index = offset >> 2;
		int n = index + byteLength >> 2;
		int result = 1;
		for (; index < n; index++) {
			result = 31 * result + floatToRawIntBits(arr[index]);
		}
		return result;
	}

	/////////// buffer

	public void set(Buffer source) {
		System.arraycopy(source.arr, 0, arr, 0, source.arr.length);
	}

	public void set(Buffer source, int sourceOffset, int destinationOffset, int byteLength) {
		System.arraycopy(source.arr, sourceOffset >> 2, arr, destinationOffset >> 2, byteLength >> 2);
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
		System.arraycopy(arr, offset >> 2, destination, 0, destination.length);
		return destination;
	}

	public float[] getFloatArray(int offset, float[] destination, int destinationIndex, int count) {
		System.arraycopy(arr, offset >> 2, destination, destinationIndex, count);
		return destination;
	}

	public void setFloatArray(int offset, float[] source) {
		System.arraycopy(source, 0, arr, offset >> 2, source.length);
	}

	public void setFloatArray(int offset, float[] source, int count) {
		System.arraycopy(source, 0, arr, offset >> 2, count);
	}

	public void setFloatArray(int offset, float[] source, int sourceIndex, int count) {
		System.arraycopy(source, sourceIndex, arr, offset >> 2, count);
	}
}
