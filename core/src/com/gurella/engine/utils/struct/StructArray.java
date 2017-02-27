package com.gurella.engine.utils.struct;

import com.gurella.engine.utils.Reflection;

public class StructArray<T extends Struct> {
	private final Buffer buffer;

	private final StructType<T> structType;
	private final int structSize;

	private int capacity;
	private int length;

	private final T temp;

	public StructArray(Class<T> type, int initialCapacity) {
		this(StructType.get(type), initialCapacity);
	}

	public StructArray(StructType<T> structType, int initialCapacity) {
		buffer = new Buffer(structType.size * initialCapacity);

		this.structType = structType;
		structSize = structType.size;

		capacity = initialCapacity;

		temp = Reflection.newInstance(structType.type);
		temp.buffer = buffer;
	}

	public StructType<T> getStructType() {
		return structType;
	}

	public int getStructSize() {
		return structSize;
	}

	public int length() {
		return length;
	}

	public int getCapacity() {
		return capacity;
	}

	public void ensureCapacity(int additionalCapacity) {
		int newCapacity = capacity + additionalCapacity;
		if (newCapacity > capacity) {
			resize(Math.max(8, newCapacity));
		}
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
		int lastItemOffset = length * structSize;
		int removedItemOffset = index * structSize;
		buffer.move(lastItemOffset, removedItemOffset, structSize);
		length--;
	}

	public void removeOrdered(int index) {
		int removedItemOffset = index * structSize;
		int nextItemOffset = removedItemOffset + structSize;
		buffer.move(nextItemOffset, removedItemOffset, (length - index) * structSize);
		length--;
	}

	public void remove(int index, int count) {
		int lastItemsOffset = (length - count) * structSize;
		int removedItemOffset = index * structSize;
		buffer.move(lastItemsOffset, removedItemOffset, structSize);
		length -= count;
	}

	public void removeOrdered(int index, int count) {
		int removedItemOffset = index * structSize;
		int nextItemsOffset = removedItemOffset + (count * structSize);
		buffer.move(nextItemsOffset, removedItemOffset, (length - index - count) * structSize);
		length -= count;
	}

	public T insert(int index) {
		int addedItemOffset = index * structSize;
		buffer.move(addedItemOffset, addedItemOffset + structSize, (length - index) * structSize);
		length++;
		return get(index);
	}

	public T insert(int index, T value) {
		int addedItemOffset = index * structSize;
		buffer.setFloatArray(value.buffer.arr, value.offset, addedItemOffset, structSize);
		length++;
		return get(index);
	}

	public T insert(int index, int count) {
		int addedItemsOffset = index * structSize;
		buffer.move(addedItemsOffset, addedItemsOffset + (structSize * count), (length - index) * structSize);
		length += count;
		return get(index);
	}

	public T insert(int index, StructArray<T> arr, int count) {
		int addedItemsOffset = index * structSize;
		buffer.setFloatArray(arr.buffer.arr, 0, addedItemsOffset, addedItemsOffset + (structSize * count));
		length += count;
		return get(index);
	}

	public T insertSafely(int index) {
		resizeIfNeeded(length + 1);
		return insert(index);
	}

	public T insertSafely(int index, int count) {
		resizeIfNeeded(length + count);
		return insert(index, count);
	}

	public T add() {
		length++;
		return get(length - 1);
	}

	public T add(int count) {
		int index = length - 1;
		length += count;
		return get(index);
	}

	public T addSafely() {
		resizeIfNeeded(length + 1);
		return add();
	}

	public T addSafely(int count) {
		resizeIfNeeded(length + count);
		return add(count);
	}

	public T pop() {
		length = Math.max(0, length - 1);
		return get(length);
	}

	public T peek() {
		return get(length - 1);
	}

	public void swap(int firstIndex, int secondIndex) {
		buffer.swap(firstIndex * structSize, secondIndex * structSize, structSize);
	}

	public void set(int index, T value) {
		buffer.setFloatArray(value.buffer.arr, value.offset, index * structSize, structSize);
	}

	public void set(StructArray<T> source, int sourceIndex, int destIndex, int count) {
		buffer.setFloatArray(source.buffer.arr, sourceIndex * structSize, destIndex * structSize, count * structSize);
	}

	public void set(int sourceIndex, int destIndex) {
		buffer.move(sourceIndex * structSize, destIndex * structSize, structSize);
	}

	public void set(int sourceIndex, int destIndex, int count) {
		buffer.move(sourceIndex * structSize, destIndex * structSize, count * structSize);
	}

	public void clear() {
		length = 0;
	}

	public void shrink() {
		resize(length);
	}

	public void truncate(int newCapacity) {
		if (capacity < newCapacity) {
			resize(newCapacity);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < length; i++) {
			builder.append(get(i).toString());
			if (i < length - 1) {
				builder.append(", ");
			}
		}
		builder.append("]");
		return builder.toString();
	}

	// TODO sort, sortRange Iterator
}
