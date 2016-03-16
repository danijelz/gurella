package com.gurella.engine.pool;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.Values;

public class BooleanArrayPool {
	public final int max;
	private final Array<boolean[]> freeObjects;

	public BooleanArrayPool() {
		this(64, Integer.MAX_VALUE);
	}

	public BooleanArrayPool(int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

	public BooleanArrayPool(int initialCapacity, int max) {
		this.max = max;
		freeObjects = new Array<boolean[]>(initialCapacity);
	}

	public boolean[] obtain(int length, int maxLength) {
		boolean[] array = find(length, maxLength);
		return array == null ? new boolean[length] : array;
	}

	private boolean[] find(int length, int maxLength) {
		int low = 0;
		int high = freeObjects.size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			boolean[] midVal = freeObjects.get(mid);

			if (midVal.length < length) {
				low = mid + 1;
			} else if (midVal.length > length) {
				high = mid - 1;
				if (high >= 0) {
					boolean[] temp = freeObjects.get(high);
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

	public void free(boolean[] object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size < max) {
			Arrays.fill(object, false);
			freeObjects.add(object);
			freeObjects.sort(ArrayComparable.instance);
		}
	}

	public void freeAll(Array<boolean[]> objects) {
		if (objects == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size >= max) {
			return;
		}

		for (int i = 0; i < objects.size && freeObjects.size >= max; i++) {
			boolean[] object = objects.get(i);
			if (object != null) {
				Arrays.fill(object, false);
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

	private static class ArrayComparable implements Comparator<boolean[]> {
		private static final ArrayComparable instance = new ArrayComparable();

		@Override
		public int compare(boolean[] o1, boolean[] o2) {
			return Values.compare(o1.length, o2.length);
		}
	}
}
