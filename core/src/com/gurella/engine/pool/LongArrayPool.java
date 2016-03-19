package com.gurella.engine.pool;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.Values;

public class LongArrayPool implements ArrayPool<long[]> {
	public final int max;
	private final Array<long[]> freeObjects;

	public LongArrayPool() {
		this(64, Integer.MAX_VALUE);
	}

	public LongArrayPool(int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

	public LongArrayPool(int initialCapacity, int max) {
		this.max = max;
		freeObjects = new Array<long[]>(initialCapacity);
	}

	@Override
	public long[] obtain(int length, int maxLength) {
		long[] array = find(length, Math.max(length, maxLength));
		return array == null ? new long[length] : array;
	}

	private long[] find(int length, int maxLength) {
		int low = 0;
		int high = freeObjects.size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			long[] midVal = freeObjects.get(mid);

			if (midVal.length < length) {
				low = mid + 1;
			} else if (midVal.length > length) {
				high = mid - 1;
				if (high >= 0) {
					long[] temp = freeObjects.get(high);
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
	public void free(long[] object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size < max) {
			Arrays.fill(object, 0);
			freeObjects.add(object);
			freeObjects.sort(ArrayComparable.instance);
		}
	}

	public void freeAll(Array<long[]> objects) {
		if (objects == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size >= max) {
			return;
		}

		for (int i = 0; i < objects.size && freeObjects.size >= max; i++) {
			long[] object = objects.get(i);
			if (object != null) {
				Arrays.fill(object, 0);
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

	private static class ArrayComparable implements Comparator<long[]> {
		private static final ArrayComparable instance = new ArrayComparable();

		@Override
		public int compare(long[] o1, long[] o2) {
			return Values.compare(o1.length, o2.length);
		}
	}
}
