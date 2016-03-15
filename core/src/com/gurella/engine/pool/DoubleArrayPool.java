package com.gurella.engine.pool;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.Values;

public class DoubleArrayPool {
	public final int max;
	private final Array<double[]> freeObjects;

	public DoubleArrayPool() {
		this(64, Integer.MAX_VALUE);
	}

	public DoubleArrayPool(int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

	public DoubleArrayPool(int initialCapacity, int max) {
		this.max = max;
		freeObjects = new Array<double[]>(initialCapacity);
	}

	public double[] obtain(int length, int maxLength) {
		double[] array = find(length, maxLength);
		return array == null ? new double[length] : array;
	}

	private double[] find(int length, int maxLength) {
		int low = 0;
		int high = freeObjects.size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			double[] midVal = freeObjects.get(mid);

			if (midVal.length < length) {
				low = mid + 1;
			} else if (midVal.length > length) {
				high = mid - 1;
				if (high >= 0) {
					double[] temp = freeObjects.get(high);
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

	public void free(double[] object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size < max) {
			freeObjects.add(object);
			freeObjects.sort(ArrayComparable.instance);
		}
	}

	public void freeAll(Array<double[]> objects) {
		if (objects == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size >= max) {
			return;
		}

		for (int i = 0; i < objects.size && freeObjects.size >= max; i++) {
			double[] object = objects.get(i);
			if (object != null) {
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

	private static class ArrayComparable implements Comparator<double[]> {
		private static final ArrayComparable instance = new ArrayComparable();

		@Override
		public int compare(double[] o1, double[] o2) {
			return Values.compare(o1.length, o2.length);
		}
	}
}
