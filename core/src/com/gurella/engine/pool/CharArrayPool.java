package com.gurella.engine.pool;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.Values;

public class CharArrayPool implements ArrayPool<char[]> {
	public final int max;
	private final Array<char[]> freeObjects;

	public CharArrayPool() {
		this(64, Integer.MAX_VALUE);
	}

	public CharArrayPool(int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

	public CharArrayPool(int initialCapacity, int max) {
		this.max = max;
		freeObjects = new Array<char[]>(initialCapacity);
	}

	@Override
	public char[] obtain(int length, int maxLength) {
		char[] array = find(length, maxLength);
		return array == null ? new char[length] : array;
	}

	private char[] find(int length, int maxLength) {
		int low = 0;
		int high = freeObjects.size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			char[] midVal = freeObjects.get(mid);

			if (midVal.length < length) {
				low = mid + 1;
			} else if (midVal.length > length) {
				high = mid - 1;
				if (high >= 0) {
					char[] temp = freeObjects.get(high);
					if (temp.length < length && midVal.length <= maxLength) {
						return freeObjects.removeIndex(high);
					}
				}
			} else {
				return freeObjects.removeIndex(mid);
			}
		}

		return null;
	}

	@Override
	public void free(char[] object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size < max) {
			Arrays.fill(object, (char) 0);
			freeObjects.add(object);
			freeObjects.sort(ArrayComparable.instance);
		}
	}

	public void freeAll(Array<char[]> objects) {
		if (objects == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size >= max) {
			return;
		}

		for (int i = 0; i < objects.size && freeObjects.size >= max; i++) {
			char[] object = objects.get(i);
			if (object != null) {
				Arrays.fill(object, (char) 0);
				freeObjects.add(object);
			}
		}

		freeObjects.sort(ArrayComparable.instance);
	}

	public void clear() {
		freeObjects.clear();
	}

	public int getFree() {
		return freeObjects.size;
	}

	private static class ArrayComparable implements Comparator<char[]> {
		private static final ArrayComparable instance = new ArrayComparable();

		@Override
		public int compare(char[] o1, char[] o2) {
			return Values.compare(o1.length, o2.length);
		}
	}
}
