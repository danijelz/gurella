package com.gurella.engine.utils.struct;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class StructArray<T extends Struct> {
	private final Buffer buffer;

	private final StructType<T> structType;
	private final int structSize;

	private int capacity;
	private int length;

	private final T shared;

	private StructArrayIterator<T> iterator1, iterator2;

	public StructArray(Class<T> type, int initialCapacity) {
		this(StructType.get(type), initialCapacity);
	}

	public StructArray(StructType<T> structType, int initialCapacity) {
		this.structType = structType;
		buffer = new FloatArrayBuffer(structType.size * initialCapacity);
		structSize = structType.size;
		capacity = initialCapacity;
		shared = structType.newInstance(buffer, 0);
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
		shared.offset = structSize * index;
		return shared;
	}

	public T get(int index, T out) {
		out.buffer = buffer;
		out.offset = structSize * index;
		return out;
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
		int removedItemsOffset = index * structSize;
		int followingItemsOffset = (length - count) * structSize;
		buffer.move(followingItemsOffset, removedItemsOffset, structSize);
		length -= count;
	}

	public void removeOrdered(int index, int count) {
		int removedItemsOffset = index * structSize;
		int followingItemsOffset = removedItemsOffset + (count * structSize);
		buffer.move(followingItemsOffset, removedItemsOffset, (length - index - count) * structSize);
		length -= count;
	}

	public T insert(int index) {
		int addedItemOffset = index * structSize;
		buffer.move(addedItemOffset, addedItemOffset + structSize, (length - index) * structSize);
		length++;
		return get(index);
	}

	public T insert(int index, T value) {
		buffer.set(value.buffer, value.offset, index * structSize, structSize);
		length++;
		return get(index);
	}

	public T insert(int index, int count) {
		int addedItemsOffset = index * structSize;
		buffer.move(addedItemsOffset, addedItemsOffset + (structSize * count), (length - index) * structSize);
		length += count;
		return get(index);
	}

	private T insert(int index, StructArray<T> source, int fromItem, int count) {
		int addedItemsOffset = index * structSize;
		buffer.move(addedItemsOffset, addedItemsOffset + (structSize * count), (length - index) * structSize);
		buffer.set(source.buffer, fromItem * structSize, index * structSize, count * structSize);
		length += count;
		return get(index);
	}

	public T insertSafely(int index) {
		resizeIfNeeded(length + 1);
		return insert(index);
	}

	public T insertSafely(int index, T value) {
		resizeIfNeeded(length + 1);
		return insert(index, value);
	}

	public T insertSafely(int index, int count) {
		resizeIfNeeded(length + count);
		return insert(index, count);
	}

	public T insertSafely(int index, StructArray<T> source, int fromItem, int count) {
		resizeIfNeeded(length + count);
		return insert(index, source, fromItem, count);
	}

	public T add() {
		length++;
		return get(length - 1);
	}

	public T add(T value) {
		buffer.set(value.buffer, value.offset, length * structSize, structSize);
		return get(length++);
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

	public T addSafely(T value) {
		resizeIfNeeded(length + 1);
		return add(value);
	}

	public T addSafely(int count) {
		resizeIfNeeded(length + count);
		return add(count);
	}

	public T addAll(StructArray<T> source) {
		return addAll(source, 0, source.length);
	}

	public T addAll(StructArray<T> source, int startIndex, int count) {
		buffer.set(source.buffer, startIndex * structSize, length * structSize, structSize * count);
		length += count;
		return get(startIndex);
	}

	public T addAllSafely(StructArray<T> source) {
		resizeIfNeeded(length + source.length);
		return addAll(source);
	}

	public T addAllSafely(StructArray<T> source, int startIndex, int count) {
		resizeIfNeeded(length + count);
		return addAll(source, startIndex, count);
	}

	public T first() {
		if (length == 0) {
			throw new IllegalStateException("Array is empty.");
		}
		return get(0);
	}

	public T first(T out) {
		if (length == 0) {
			throw new IllegalStateException("Array is empty.");
		}
		return get(0, out);
	}

	public T pop() {
		if (length == 0) {
			throw new IllegalStateException("Array is empty.");
		}
		length = Math.max(0, length - 1);
		return get(length);
	}

	public T pop(T out) {
		if (length == 0) {
			throw new IllegalStateException("Array is empty.");
		}
		length = Math.max(0, length - 1);
		return get(length, out);
	}

	public T peek() {
		if (length == 0) {
			throw new IllegalStateException("Array is empty.");
		}
		return get(length - 1);
	}

	public T peek(T out) {
		if (length == 0) {
			throw new IllegalStateException("Array is empty.");
		}
		return get(length - 1, out);
	}

	public void swap(int fromIndex, int toIndex) {
		buffer.swap(fromIndex * structSize, toIndex * structSize, structSize);
	}

	public void set(int index, T value) {
		buffer.set(value.buffer, value.offset, index * structSize, structSize);
	}

	public void set(int index, StructArray<T> source, int sourceIndex, int count) {
		buffer.set(source.buffer, sourceIndex * structSize, index * structSize, count * structSize);
	}

	public void move(int fromIndex, int toIndex) {
		buffer.move(fromIndex * structSize, toIndex * structSize, structSize);
	}

	public void move(int fromIndex, int toIndex, int count) {
		buffer.move(fromIndex * structSize, toIndex * structSize, count * structSize);
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

	public Iterator<T> iterator() {
		if (iterator1 == null) {
			iterator1 = new StructArrayIterator<T>(this, true);
			iterator2 = new StructArrayIterator<T>(this, true);
		}

		if (!iterator1.valid) {
			iterator1.index = 0;
			iterator1.valid = true;
			iterator2.valid = false;
			return iterator1;
		}

		iterator2.index = 0;
		iterator2.valid = true;
		iterator1.valid = false;

		return iterator2;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < length; i++) {
			builder.append(get(i).toString());
			if (i < length - 1) {
				builder.append("\n");
			}
		}
		builder.append("]");
		return builder.toString();
	}

	// TODO sort, sortRange

	public static class StructArrayIterator<T extends Struct> implements Iterator<T>, Iterable<T> {
		private final StructArray<T> array;
		private final boolean allowRemove;
		private int index;
		private boolean valid = true;

		private final T shared;

		public StructArrayIterator(StructArray<T> array) {
			this(array, true);
		}

		public StructArrayIterator(StructArray<T> array, boolean allowRemove) {
			this.array = array;
			this.allowRemove = allowRemove;
			shared = array.structType.newInstance(array.buffer, 0);
		}

		@Override
		public boolean hasNext() {
			if (!valid) {
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			}

			return index < array.length;
		}

		@Override
		public T next() {
			if (index >= array.length) {
				throw new NoSuchElementException(String.valueOf(index));
			}

			if (!valid) {
				throw new RuntimeException("#iterator() cannot be used nested.");
			}

			return array.get(index++, shared);
		}

		@Override
		public void remove() {
			if (!allowRemove) {
				throw new RuntimeException("Remove not allowed.");
			}

			index--;
			array.remove(index);
		}

		public void reset() {
			index = 0;
		}

		@Override
		public Iterator<T> iterator() {
			return this;
		}
	}
}
