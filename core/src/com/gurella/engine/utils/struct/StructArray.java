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
	private float[] temp;

	private StructArrayIterator<T> iterator1, iterator2;

	public StructArray(Class<T> type, int initialCapacity) {
		this(StructType.get(type), initialCapacity);
	}

	public StructArray(StructType<T> structType, int initialCapacity) {
		this.structType = structType;
		buffer = new UnsafeBuffer(structType.size * initialCapacity);
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
		validateIndex(index);
		shared.offset = structSize * index;
		return shared;
	}

	private void validateIndex(int index) {
		if (index >= length) {
			throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + length);
		}
	}

	public T get(int index, T out) {
		validateIndex(index);
		out.buffer = buffer;
		out.offset = structSize * index;
		return out;
	}

	public T getCopy(int index, T out) {
		validateIndex(index);
		out.buffer.set(buffer, structSize * index, out.offset, structSize);
		return out;
	}

	public void remove(int index) {
		validateIndex(index);
		int lastItemOffset = length * structSize;
		int removedItemOffset = index * structSize;
		buffer.move(lastItemOffset, removedItemOffset, structSize);
		length--;
	}

	public void removeOrdered(int index) {
		validateIndex(index);
		int removedItemOffset = index * structSize;
		int nextItemOffset = removedItemOffset + structSize;
		buffer.move(nextItemOffset, removedItemOffset, (length - index) * structSize);
		length--;
	}

	public void remove(int index, int count) {
		validateIndex(index + count - 1);
		int removedItemsOffset = index * structSize;
		int followingItemsOffset = (length - count) * structSize;
		buffer.move(followingItemsOffset, removedItemsOffset, structSize);
		length -= count;
	}

	public void removeOrdered(int index, int count) {
		validateIndex(index + count - 1);
		int removedItemsOffset = index * structSize;
		int followingItemsOffset = removedItemsOffset + (count * structSize);
		buffer.move(followingItemsOffset, removedItemsOffset, (length - index - count) * structSize);
		length -= count;
	}

	public T insert(int index) {
		validateIndex(index);
		resizeIfNeeded(length + 1);
		int addedItemOffset = index * structSize;
		buffer.move(addedItemOffset, addedItemOffset + structSize, (length - index) * structSize);
		length++;
		return get(index);
	}

	public T insert(int index, T value) {
		validateIndex(index);
		resizeIfNeeded(length + 1);
		buffer.set(value.buffer, value.offset, index * structSize, structSize);
		length++;
		return get(index);
	}

	public T insert(int index, int count) {
		validateIndex(index);
		resizeIfNeeded(length + count);
		int addedItemsOffset = index * structSize;
		buffer.move(addedItemsOffset, addedItemsOffset + (structSize * count), (length - index) * structSize);
		length += count;
		return get(index);
	}

	public T insert(int index, StructArray<T> source, int fromIndex, int count) {
		validateIndex(index);
		validateType(source.structType);
		source.validateIndex(fromIndex + count - 1);
		resizeIfNeeded(length + count);
		int addedItemsOffset = index * structSize;
		buffer.move(addedItemsOffset, addedItemsOffset + (structSize * count), (length - index) * structSize);
		buffer.set(source.buffer, fromIndex * structSize, index * structSize, count * structSize);
		length += count;
		return get(index);
	}

	private void validateType(StructType<T> otherStructType) {
		if(structType != otherStructType) {
			throw new IllegalArgumentException("Invalid arrat type.");
		}
	}

	public T add() {
		resizeIfNeeded(length + 1);
		return get(length++);
	}

	public T add(T value) {
		resizeIfNeeded(length + 1);
		buffer.set(value.buffer, value.offset, length * structSize, structSize);
		return get(length++);
	}

	public T add(int count) {
		resizeIfNeeded(length + count);
		int index = length - 1;
		length += count;
		return get(index);
	}

	public T addAll(StructArray<T> source) {
		return addAll(source, 0, source.length);
	}

	public T addAll(StructArray<T> source, int fromIndex, int count) {
		validateType(source.structType);
		source.validateIndex(fromIndex + count - 1);
		int destinationOffset = length * structSize;
		int index = length - 1;
		resizeIfNeeded(length);
		buffer.set(source.buffer, fromIndex * structSize, destinationOffset, structSize * count);
		length += count;
		return get(index);
	}

	public T first() {
		validateNotEmpty();
		return get(0);
	}

	private void validateNotEmpty() {
		if (length == 0) {
			throw new IllegalStateException("Array is empty.");
		}
	}

	public T first(T out) {
		validateNotEmpty();
		return get(0, out);
	}

	public T firstCopy(T out) {
		validateNotEmpty();
		return getCopy(0, out);
	}

	public T pop() {
		validateNotEmpty();
		length = Math.max(0, length - 1);
		return get(length);
	}

	public T pop(T out) {
		validateNotEmpty();
		length -= 1;
		return get(length, out);
	}

	public T popCopy(T out) {
		validateNotEmpty();
		length -= 1;
		return getCopy(length, out);
	}

	public T peek() {
		validateNotEmpty();
		return get(length - 1);
	}

	public T peek(T out) {
		validateNotEmpty();
		return get(length - 1, out);
	}

	public T peekCopy(T out) {
		validateNotEmpty();
		return getCopy(length - 1, out);
	}

	public void swap(int fromIndex, int toIndex) {
		validateIndex(fromIndex);
		validateIndex(toIndex);

		if (temp == null) {
			temp = new float[structSize >> 2];
		}

		buffer.swap(fromIndex * structSize, toIndex * structSize, temp);
	}

	public void set(int index, T value) {
		validateIndex(index);
		buffer.set(value.buffer, value.offset, index * structSize, structSize);
	}

	public void set(int index, StructArray<T> source, int fromIndex, int count) {
		validateIndex(index - 1);
		validateType(source.structType);
		source.validateIndex(fromIndex + count - 1);
		resizeIfNeeded(length + count);
		buffer.set(source.buffer, fromIndex * structSize, index * structSize, count * structSize);
		length = Math.max(length, index + count);
	}

	public void move(int fromIndex, int toIndex) {
		validateIndex(fromIndex);
		validateIndex(toIndex);
		buffer.move(fromIndex * structSize, toIndex * structSize, structSize);
	}

	public void move(int fromIndex, int toIndex, int count) {
		validateIndex(fromIndex);
		validateIndex(toIndex);
		buffer.move(fromIndex * structSize, toIndex * structSize, count * structSize);
	}

	public void clear() {
		length = 0;
	}

	public void shrink() {
		resize(length);
	}

	public void truncate(int newLength) {
		if (length > newLength) {
			length = newLength;
		}
	}

	public void setLength(int newLength) {
		resizeIfNeeded(newLength);
		length = newLength;
	}

	public void reverse() {
		reverse(0, length - 1);
	}

	public void reverse(int startIndex, int endIndex) {
		int left = startIndex;
		int right = endIndex;

		int len = right - left;
		if (len <= 0) {
			return;
		}

		if (temp == null) {
			temp = new float[structSize >> 2];
		}

		while (left < right) {
			buffer.swap(structSize * left++, structSize * right--, temp);
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		StructArray<?> other = (StructArray<?>) obj;
		return length == other.length && buffer.equals(0, other.buffer, 0, length * structSize);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode() + buffer.hashCode(0, length * structSize) * 31;
	}

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

		public T next(T out) {
			if (index >= array.length) {
				throw new NoSuchElementException(String.valueOf(index));
			}

			if (!valid) {
				throw new RuntimeException("#iterator() cannot be used nested.");
			}

			return array.get(index++, out);
		}

		public T nextCopy(T out) {
			if (index >= array.length) {
				throw new NoSuchElementException(String.valueOf(index));
			}

			if (!valid) {
				throw new RuntimeException("#iterator() cannot be used nested.");
			}

			return array.getCopy(index++, out);
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
