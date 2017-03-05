package com.gurella.engine.utils.struct;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class StructArrayView<T extends Struct> {
	StructArray<T> array;
	int offsetIndex;
	int length;

	private StructArrayViewIterator<T> iterator1, iterator2;

	protected StructArrayView() {
	}

	public StructArrayView(StructArray<T> array, int offsetIndex, int length) {
		this.array = array;
		this.offsetIndex = offsetIndex;
		this.length = length;
	}

	public int length() {
		return length;
	}

	public T get(int index) {
		validateIndex(index);
		return array.get(offsetIndex + index);
	}

	private void validateIndex(int index) {
		if (index >= length) {
			throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + length);
		}
	}

	public T get(int index, T out) {
		validateIndex(index);
		return array.get(offsetIndex + index, out);
	}

	public T getCopy(int index, T out) {
		validateIndex(index);
		return array.getCopy(offsetIndex + index, out);
	}

	public void remove(int index) {
		validateIndex(index);
		array.remove(index + offsetIndex);
		length--;
	}

	public void removeOrdered(int index) {
		validateIndex(index);
		array.removeOrdered(index + offsetIndex);
		length--;
	}

	public void remove(int index, int count) {
		validateIndex(index + count - 1);
		array.remove(index + offsetIndex, count);
		length -= count;
	}

	public void removeOrdered(int index, int count) {
		validateIndex(index + count - 1);
		array.removeOrdered(index + offsetIndex, count);
		length -= count;
	}

	public T insert(int index) {
		validateIndex(index);
		length++;
		return array.insert(index + offsetIndex);
	}

	public T insert(int index, T value) {
		validateIndex(index);
		length++;
		return array.insert(index + offsetIndex, value);
	}

	public T insert(int index, int count) {
		validateIndex(index);
		length += count;
		return array.insert(index + offsetIndex, count);
	}

	public T insert(int index, StructArray<T> source, int fromIndex, int count) {
		validateIndex(index);
		length += count;
		return array.insert(index + offsetIndex, source, fromIndex, count);
	}

	public T add() {
		length++;
		return array.add();
	}

	public T add(T value) {
		length++;
		return array.add(value);
	}

	public T add(int count) {
		length += count;
		return add(count);
	}

	public T addAll(StructArray<T> source) {
		return addAll(source, 0, source.length());
	}

	public T addAll(StructArray<T> source, int fromIndex, int count) {
		length += count;
		return array.addAll(source, fromIndex, count);
	}

	public T first() {
		return get(0);
	}

	public T first(T out) {
		return get(0, out);
	}

	public T firstCopy(T out) {
		return getCopy(0, out);
	}

	public T pop() {
		length = Math.max(0, length - 1);
		return get(length);
	}

	public T pop(T out) {
		length -= 1;
		return get(length, out);
	}

	public T popCopy(T out) {
		length -= 1;
		return getCopy(length, out);
	}

	public T peek() {
		return get(length - 1);
	}

	public T peek(T out) {
		return get(length - 1, out);
	}

	public T peekCopy(T out) {
		return getCopy(length - 1, out);
	}

	public void swap(int fromIndex, int toIndex) {
		swap(fromIndex, toIndex, 1);
	}

	public void swap(int fromIndex, int toIndex, int count) {
		validateIndex(fromIndex);
		validateIndex(toIndex + count - 1);
		array.swap(fromIndex + offsetIndex, toIndex + offsetIndex, count);
	}

	public void swap(int fromIndex, int toIndex, float[] tempStorage) {
		swap(fromIndex, toIndex, 1, tempStorage);
	}

	public void swap(int fromIndex, int toIndex, int count, float[] tempStorage) {
		validateIndex(fromIndex);
		validateIndex(toIndex + count - 1);
		array.swap(fromIndex + offsetIndex, toIndex + offsetIndex, count, tempStorage);
	}

	public void set(int index, T value) {
		validateIndex(index);
		array.set(index + offsetIndex, value);
	}

	public void set(int index, StructArray<T> source, int fromIndex, int count) {
		validateIndex(index - 1);
		array.set(index + offsetIndex, source, fromIndex, count);
		length = Math.max(length, index + count);
	}

	public void move(int fromIndex, int toIndex) {
		validateIndex(fromIndex);
		validateIndex(toIndex);
		array.move(fromIndex + offsetIndex, toIndex + offsetIndex);
	}

	public void move(int fromIndex, int toIndex, int count) {
		validateIndex(fromIndex);
		validateIndex(toIndex);
		array.move(fromIndex + offsetIndex, toIndex + offsetIndex, count);
	}

	public int indexOf(T value) {
		return array.indexOf(value) - offsetIndex;
	}

	public void reverse() {
		reverse(0, length - 1);
	}

	public void reverse(int startIndex, int endIndex) {
		validateIndex(startIndex);
		validateIndex(endIndex);
		array.reverse(startIndex + offsetIndex, endIndex + offsetIndex);
	}

	public void reverse(int startIndex, int endIndex, float[] tempStorage) {
		validateIndex(startIndex);
		validateIndex(endIndex);
		array.reverse(startIndex + offsetIndex, endIndex + offsetIndex, tempStorage);
	}

	public Iterator<T> iterator() {
		if (iterator1 == null) {
			iterator1 = new StructArrayViewIterator<T>(this, true);
			iterator2 = new StructArrayViewIterator<T>(this, true);
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

		int structSize = array.structSize;
		StructArrayView<?> other = (StructArrayView<?>) obj;
		return length == other.length && array.structType == other.array.structType && array.buffer.equals(
				offsetIndex * structSize, other.array.buffer, other.offsetIndex * structSize, length * structSize);
	}

	@Override
	public int hashCode() {
		int structSize = array.structSize;
		return getClass().hashCode() + array.buffer.hashCode(offsetIndex * structSize, length * structSize) * 31;
	}

	public static class StructArrayViewIterator<T extends Struct> implements Iterator<T>, Iterable<T> {
		private final StructArrayView<T> view;
		private final boolean allowRemove;
		private int index;
		private boolean valid = true;

		private final T shared;

		public StructArrayViewIterator(StructArrayView<T> view) {
			this(view, true);
		}

		public StructArrayViewIterator(StructArrayView<T> view, boolean allowRemove) {
			this.view = view;
			this.allowRemove = allowRemove;
			shared = view.array.newStruct(view.offsetIndex);
		}

		@Override
		public boolean hasNext() {
			if (!valid) {
				throw new GdxRuntimeException("#iterator() cannot be used nested.");
			}

			return index < view.length;
		}

		@Override
		public T next() {
			if (index >= view.length) {
				throw new NoSuchElementException(String.valueOf(index));
			}

			if (!valid) {
				throw new RuntimeException("#iterator() cannot be used nested.");
			}

			return view.get(index++, shared);
		}

		public T next(T out) {
			if (index >= view.length) {
				throw new NoSuchElementException(String.valueOf(index));
			}

			if (!valid) {
				throw new RuntimeException("#iterator() cannot be used nested.");
			}

			return view.get(index++, out);
		}

		public T nextCopy(T out) {
			if (index >= view.length) {
				throw new NoSuchElementException(String.valueOf(index));
			}

			if (!valid) {
				throw new RuntimeException("#iterator() cannot be used nested.");
			}

			return view.getCopy(index++, out);
		}

		@Override
		public void remove() {
			if (!allowRemove) {
				throw new RuntimeException("Remove not allowed.");
			}

			index--;
			view.remove(index);
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
