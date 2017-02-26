package com.gurella.engine.utils.struct;

import com.gurella.engine.utils.Reflection;

public class StructArray<T extends Struct> {
	private final Buffer buffer;

	private int capacity;
	private int size;

	private final T temp;
	private final int structSize;

	public StructArray(Class<T> type, int initialSize) {
		this(StructType.get(type), initialSize);
	}

	public StructArray(StructType<T> structType, int initialSize) {
		capacity = structType.size * initialSize;
		buffer = new Buffer(capacity);

		temp = Reflection.newInstance(structType.type);
		temp.buffer = buffer;
		structSize = structType.size;
	}

	public int size() {
		return size;
	}

	private void resizeIfNeeded(int newCapacity) {
		if (capacity < newCapacity) {
			resize(Math.max(8, (int) (newCapacity * 1.75f)));
		}
	}

	public void resize(int newCapacity) {
		buffer.resize(newCapacity * structSize);
		capacity = newCapacity;
	}

	T get(int index) {
		temp.offset = structSize * index;
		return temp;
	}

	public T get(int index, T struct) {
		struct.buffer = buffer;
		struct.offset = structSize * index;
		return temp;
	}

	public void remove(int index) {
		float[] buffer = this.buffer.buffer;
		int lastItemOffset = size * structSize;
		int removedItemOffset = index * structSize;
		System.arraycopy(buffer, lastItemOffset, buffer, removedItemOffset, structSize);
		size--;
	}

	public void removeOrdered(int index) {
		float[] buffer = this.buffer.buffer;
		int removedItemOffset = index * structSize;
		int nextItemOffset = removedItemOffset + structSize;
		int length = (size - index) * structSize;
		System.arraycopy(buffer, nextItemOffset, buffer, removedItemOffset, length);
		size--;
	}

	public void remove(int index, int count) {
		float[] buffer = this.buffer.buffer;
		int lastItemsOffset = (size - count) * structSize;
		int removedItemOffset = index * structSize;
		System.arraycopy(buffer, lastItemsOffset, buffer, removedItemOffset, count * structSize);
		size -= count;
	}

	public void removeOrdered(int index, int count) {
		float[] buffer = this.buffer.buffer;
		int removedItemOffset = index * structSize;
		int nextItemsOffset = removedItemOffset + (count * structSize);
		int length = (size - index - count) * structSize;
		System.arraycopy(buffer, nextItemsOffset, buffer, removedItemOffset, length);
		size -= count;
	}

	public void insert(int index) {
		float[] buffer = this.buffer.buffer;
		int addedItemOffset = index * structSize;
		int length = (size - index) * structSize;
		System.arraycopy(buffer, addedItemOffset, buffer, addedItemOffset + structSize, length);
		size++;
	}

	public void insert(int index, int count) {
		float[] buffer = this.buffer.buffer;
		int addedItemOffset = index * structSize;
		int length = (size - index) * structSize;
		System.arraycopy(buffer, addedItemOffset, buffer, addedItemOffset + (structSize * count), length);
		size += count;
	}

	public void insertSafely(int index) {
		resizeIfNeeded(size + 1);
		float[] buffer = this.buffer.buffer;
		int addedItemOffset = index * structSize;
		int length = (size - index) * structSize;
		System.arraycopy(buffer, addedItemOffset, buffer, addedItemOffset + structSize, length);
		size++;
	}

	public void insertSafely(int index, int count) {
		resizeIfNeeded(size + count);
		float[] buffer = this.buffer.buffer;
		int addedItemOffset = index * structSize;
		int length = (size - index) * structSize;
		System.arraycopy(buffer, addedItemOffset, buffer, addedItemOffset + (structSize * count), length);
		size += count;
	}

	public void add() {
		size++;
	}

	public void add(int count) {
		size += count;
	}

	public void addSafely() {
		resizeIfNeeded(size + 1);
		size++;
	}

	public void addSafely(int count) {
		resizeIfNeeded(size + count);
		size += count;
	}
}
