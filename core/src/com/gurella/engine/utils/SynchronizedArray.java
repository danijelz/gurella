package com.gurella.engine.utils;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;

public class SynchronizedArray<T> extends ArrayExt<T> {
	private final Object mutex = new Object();

	public SynchronizedArray() {
		super();
	}

	public SynchronizedArray(Array<? extends T> array) {
		super(array);
	}

	public SynchronizedArray(boolean ordered, int capacity, Class<T> arrayType) {
		super(ordered, capacity, arrayType);
	}

	public SynchronizedArray(boolean ordered, int capacity) {
		super(ordered, capacity);
	}

	public SynchronizedArray(boolean ordered, T[] array, int start, int count) {
		super(ordered, array, start, count);
	}

	public SynchronizedArray(Class<T> arrayType) {
		super(arrayType);
	}

	public SynchronizedArray(int capacity) {
		super(capacity);
	}

	public SynchronizedArray(T[] array) {
		super(array);
	}

	@Override
	public void add(T value) {
		synchronized (mutex) {
			super.add(value);
		}
	}

	@Override
	public void addAll(Array<? extends T> array) {
		synchronized (mutex) {
			super.addAll(array);
		}
	}

	@Override
	public void addAll(Array<? extends T> array, int start, int count) {
		synchronized (mutex) {
			super.addAll(array, start, count);
		}
	}

	@Override
	public void addAll(@SuppressWarnings("unchecked") T... array) {
		synchronized (mutex) {
			super.addAll(array);
		}
	}

	@Override
	public void addAll(T[] array, int start, int count) {
		synchronized (mutex) {
			super.addAll(array, start, count);
		}
	}

	@Override
	public T get(int index) {
		synchronized (mutex) {
			return super.get(index);
		}
	}

	@Override
	public void set(int index, T value) {
		synchronized (mutex) {
			super.set(index, value);
		}
	}

	@Override
	public void insert(int index, T value) {
		synchronized (mutex) {
			super.insert(index, value);
		}
	}

	@Override
	public void swap(int first, int second) {
		synchronized (mutex) {
			super.swap(first, second);
		}
	}

	@Override
	public boolean contains(T value, boolean identity) {
		synchronized (mutex) {
			return super.contains(value, identity);
		}
	}

	@Override
	public int indexOf(T value, boolean identity) {
		synchronized (mutex) {
			return super.indexOf(value, identity);
		}
	}

	@Override
	public int lastIndexOf(T value, boolean identity) {
		synchronized (mutex) {
			return super.lastIndexOf(value, identity);
		}
	}

	@Override
	public boolean removeValue(T value, boolean identity) {
		synchronized (mutex) {
			return super.removeValue(value, identity);
		}
	}

	@Override
	public T removeIndex(int index) {
		synchronized (mutex) {
			return super.removeIndex(index);
		}
	}

	@Override
	public void removeRange(int start, int end) {
		synchronized (mutex) {
			super.removeRange(start, end);
		}
	}

	@Override
	public boolean removeAll(Array<? extends T> array, boolean identity) {
		synchronized (mutex) {
			return super.removeAll(array, identity);
		}
	}

	@Override
	public T pop() {
		synchronized (mutex) {
			return super.pop();
		}
	}

	@Override
	public T peek() {
		synchronized (mutex) {
			return super.peek();
		}
	}

	@Override
	public T first() {
		synchronized (mutex) {
			return super.first();
		}
	}

	@Override
	public void clear() {
		synchronized (mutex) {
			super.clear();
		}
	}

	@Override
	public T[] shrink() {
		synchronized (mutex) {
			return super.shrink();
		}
	}

	@Override
	public T[] ensureCapacity(int additionalCapacity) {
		synchronized (mutex) {
			return super.ensureCapacity(additionalCapacity);
		}
	}

	@Override
	protected T[] resize(int newSize) {
		synchronized (mutex) {
			return super.resize(newSize);
		}
	}

	@Override
	public void sort() {
		synchronized (mutex) {
			super.sort();
		}
	}

	@Override
	public void sort(Comparator<? super T> comparator) {
		synchronized (mutex) {
			super.sort(comparator);
		}
	}

	@Override
	public T selectRanked(Comparator<T> comparator, int kthLowest) {
		synchronized (mutex) {
			return super.selectRanked(comparator, kthLowest);
		}
	}

	@Override
	public int selectRankedIndex(Comparator<T> comparator, int kthLowest) {
		synchronized (mutex) {
			return super.selectRankedIndex(comparator, kthLowest);
		}
	}

	@Override
	public void reverse() {
		synchronized (mutex) {
			super.reverse();
		}
	}

	@Override
	public void shuffle() {
		synchronized (mutex) {
			super.shuffle();
		}
	}

	@Override
	public void truncate(int newSize) {
		synchronized (mutex) {
			super.truncate(newSize);
		}
	}

	@Override
	public T random() {
		synchronized (mutex) {
			return super.random();
		}
	}

	@Override
	public T[] toArray() {
		synchronized (mutex) {
			return super.toArray();
		}
	}

	@Override
	public <V> V[] toArray(@SuppressWarnings("rawtypes") Class type) {
		synchronized (mutex) {
			return super.toArray(type);
		}
	}

	@Override
	public int hashCode() {
		synchronized (mutex) {
			return 31 + super.hashCode();
		}
	}

	@Override
	public boolean equals(Object object) {
		synchronized (mutex) {
			if (this == object) {
				return true;
			}
			if (!(object instanceof SynchronizedArray)) {
				return false;
			}
			return super.equals(object);
		}
	}

	@Override
	public String toString() {
		synchronized (mutex) {
			return super.toString();
		}
	}

	@Override
	public String toString(String separator) {
		synchronized (mutex) {
			return super.toString(separator);
		}
	}

	static public <T> SynchronizedArray<T> of(Class<T> arrayType) {
		return new SynchronizedArray<T>(arrayType);
	}

	static public <T> SynchronizedArray<T> of(boolean ordered, int capacity, Class<T> arrayType) {
		return new SynchronizedArray<T>(ordered, capacity, arrayType);
	}

	static public <T> SynchronizedArray<T> with(@SuppressWarnings("unchecked") T... array) {
		return new SynchronizedArray<T>(array);
	}
}
