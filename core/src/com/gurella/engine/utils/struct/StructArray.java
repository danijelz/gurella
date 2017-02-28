package com.gurella.engine.utils.struct;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class StructArray<T extends Struct> {
	private final BaseBuffer buffer;

	private final StructType<T> structType;
	private final int structSize;

	private int capacity;
	private int length;

	private final T temp;

	private StructArrayIterator<T> iterator1, iterator2;

	public StructArray(Class<T> type, int initialCapacity) {
		this(StructType.get(type), initialCapacity);
	}

	public StructArray(StructType<T> structType, int initialCapacity) {
		this.structType = structType;
		buffer = new FloatArrayBuffer(structType.size * initialCapacity);
		structSize = structType.size;
		capacity = initialCapacity;
		temp = structType.newInstance(buffer, 0);
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

	public T insert(StructArray<T> source, int sourceIndex, int destinationIndex, int count) {
		int addedItemsOffset = destinationIndex * structSize;
		buffer.setFloatArray(source.buffer.arr, 0, addedItemsOffset, addedItemsOffset + (structSize * count));
		length += count;
		return get(sourceIndex);
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

	public T insertSafely(StructArray<T> source, int sourceIndex, int destinationIndex, int count) {
		resizeIfNeeded(length + count);
		return insert(source, sourceIndex, destinationIndex, count);
	}

	public T add() {
		length++;
		return get(length - 1);
	}

	public T add(T value) {
		int addedItemOffset = length * structSize;
		buffer.setFloatArray(value.buffer.arr, value.offset, addedItemOffset, structSize);
		return get(length++);
	}

	public T addSafely() {
		resizeIfNeeded(length + 1);
		return add();
	}

	public T addSafely(T value) {
		resizeIfNeeded(length + 1);
		return add(value);
	}

	public T add(int count) {
		int index = length - 1;
		length += count;
		return get(index);
	}

	public T addSafely(int count) {
		resizeIfNeeded(length + count);
		return add(count);
	}

	public T add(StructArray<T> source, int sourceIndex, int count) {
		int addedItemsOffset = length * structSize;
		buffer.setFloatArray(source.buffer.arr, 0, addedItemsOffset, addedItemsOffset + (structSize * count));
		length += count;
		return get(sourceIndex);
	}

	public T addSafely(StructArray<T> source, int sourceIndex, int count) {
		resizeIfNeeded(length + count);
		return add(source, sourceIndex, count);
	}

	public T addAll(StructArray<T> source) {
		return add(source, 0, source.length);
	}

	public T addAllSafely(StructArray<T> source) {
		resizeIfNeeded(length + source.length);
		return addAll(source);
	}

	// TODO public T addSafely(int index, StructArray<T> arr, int count) {

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
				builder.append(", ");
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

		private final T temp;

		public StructArrayIterator(StructArray<T> array) {
			this(array, true);
		}

		public StructArrayIterator(StructArray<T> array, boolean allowRemove) {
			this.array = array;
			this.allowRemove = allowRemove;
			temp = array.structType.newInstance(array.buffer, 0);
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

			return array.get(index++, temp);
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
