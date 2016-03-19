package com.gurella.engine.pool;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.Values;

public class FloatArrayPool implements ArrayPool<float[]> {
	public final int max;
	private final Array<float[]> freeObjects;

	public FloatArrayPool() {
		this(64, Integer.MAX_VALUE);
	}

	public FloatArrayPool(int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

	public FloatArrayPool(int initialCapacity, int max) {
		this.max = max;
		freeObjects = new Array<float[]>(initialCapacity);
	}

	@Override
	public float[] obtain(int length, int maxLength) {
		float[] array = find(length, Math.max(length, maxLength));
		return array == null ? new float[length] : array;
	}

	private float[] find(int length, int maxLength) {
		int low = 0;
		int high = freeObjects.size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			float[] midVal = freeObjects.get(mid);

			if (midVal.length < length) {
				low = mid + 1;
			} else if (midVal.length > length) {
				high = mid - 1;
				if (high >= 0) {
					float[] temp = freeObjects.get(high);
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
	public void free(float[] object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size < max) {
			Arrays.fill(object, 0);
			freeObjects.add(object);
			freeObjects.sort(ArrayComparable.instance);
		}
	}

	public void freeAll(Array<float[]> objects) {
		if (objects == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size >= max) {
			return;
		}

		for (int i = 0; i < objects.size && freeObjects.size >= max; i++) {
			float[] object = objects.get(i);
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

	private static class ArrayComparable implements Comparator<float[]> {
		private static final ArrayComparable instance = new ArrayComparable();

		@Override
		public int compare(float[] o1, float[] o2) {
			return Values.compare(o1.length, o2.length);
		}
	}
}
