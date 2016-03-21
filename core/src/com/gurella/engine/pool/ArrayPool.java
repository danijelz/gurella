package com.gurella.engine.pool;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.Values;

public abstract class ArrayPool<T> {
	public final int max;
	private final Array<T> freeObjects;
	private ArrayComparable comparable = new ArrayComparable();

	public ArrayPool() {
		this(64, Integer.MAX_VALUE);
	}

	public ArrayPool(int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

	public ArrayPool(int initialCapacity, int max) {
		this.max = max;
		freeObjects = new Array<T>(initialCapacity);
	}

	public T obtain(int length, int maxLength) {
		T array = find(length, Math.max(length, maxLength));
		return array == null ? newObject(length) : array;
	}

	protected abstract int length(T array);

	protected abstract T newObject(int length);

	protected abstract void clear(T array);

	private T find(int length, int maxLength) {
		int low = 0;
		int high = freeObjects.size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			T midVal = freeObjects.get(mid);

			if (length(midVal) < length) {
				low = mid + 1;
			} else if (length(midVal) > length) {
				high = mid - 1;
				if (high >= 0) {
					T temp = freeObjects.get(high);
					if (length(temp) < length && length(midVal) <= maxLength) {
						return freeObjects.removeIndex(high);
					}
				}
			} else {
				return freeObjects.removeIndex(mid);
			}
		}

		return null;
	}

	public void free(T object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size < max) {
			clear(object);
			freeObjects.add(object);
			freeObjects.sort(comparable);
		}
	}

	public void freeAll(Array<T> objects) {
		if (objects == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size >= max) {
			return;
		}

		for (int i = 0; i < objects.size && freeObjects.size >= max; i++) {
			T object = objects.get(i);
			if (object != null) {
				clear(object);
				freeObjects.add(object);
			}
		}

		freeObjects.sort(comparable);
	}

	public void clear() {
		freeObjects.clear();
	}

	public int getFree() {
		return freeObjects.size;
	}

	private class ArrayComparable implements Comparator<T> {
		@Override
		public int compare(T o1, T o2) {
			return Values.compare(length(o1), length(o2));
		}
	}
}
