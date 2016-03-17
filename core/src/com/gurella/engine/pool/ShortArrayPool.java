package com.gurella.engine.pool;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.Values;

public class ShortArrayPool implements ArrayPool<short[]> {
	public final int max;
	private final Array<short[]> freeObjects;

	public ShortArrayPool() {
		this(64, Integer.MAX_VALUE);
	}

	public ShortArrayPool(int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

	public ShortArrayPool(int initialCapacity, int max) {
		this.max = max;
		freeObjects = new Array<short[]>(initialCapacity);
	}

	@Override
	public short[] obtain(int length, int maxLength) {
		short[] array = find(length, maxLength);
		return array == null ? new short[length] : array;
	}

	private short[] find(int length, int maxLength) {
		int low = 0;
		int high = freeObjects.size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			short[] midVal = freeObjects.get(mid);

			if (midVal.length < length) {
				low = mid + 1;
			} else if (midVal.length > length) {
				high = mid - 1;
				if (high >= 0) {
					short[] temp = freeObjects.get(high);
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
	public void free(short[] object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size < max) {
			Arrays.fill(object, (short) 0);
			freeObjects.add(object);
			freeObjects.sort(ArrayComparable.instance);
		}
	}

	public void freeAll(Array<short[]> objects) {
		if (objects == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size >= max) {
			return;
		}

		for (int i = 0; i < objects.size && freeObjects.size >= max; i++) {
			short[] object = objects.get(i);
			if (object != null) {
				Arrays.fill(object, (short) 0);
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

	private static class ArrayComparable implements Comparator<short[]> {
		private static final ArrayComparable instance = new ArrayComparable();

		@Override
		public int compare(short[] o1, short[] o2) {
			return Values.compare(o1.length, o2.length);
		}
	}
}
