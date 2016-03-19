package com.gurella.engine.pool;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.Values;

public class ByteArrayPool implements ArrayPool<byte[]> {
	public final int max;
	private final Array<byte[]> freeObjects;

	public ByteArrayPool() {
		this(64, Integer.MAX_VALUE);
	}

	public ByteArrayPool(int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

	public ByteArrayPool(int initialCapacity, int max) {
		this.max = max;
		freeObjects = new Array<byte[]>(initialCapacity);
	}

	@Override
	public byte[] obtain(int length, int maxLength) {
		byte[] array = find(length, Math.max(length, maxLength));
		return array == null ? new byte[length] : array;
	}

	private byte[] find(int length, int maxLength) {
		int low = 0;
		int high = freeObjects.size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			byte[] midVal = freeObjects.get(mid);

			if (midVal.length < length) {
				low = mid + 1;
			} else if (midVal.length > length) {
				high = mid - 1;
				if (high >= 0) {
					byte[] temp = freeObjects.get(high);
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
	public void free(byte[] object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size < max) {
			Arrays.fill(object, (byte) 0);
			freeObjects.add(object);
			freeObjects.sort(ArrayComparable.instance);
		}
	}

	public void freeAll(Array<byte[]> objects) {
		if (objects == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size >= max) {
			return;
		}

		for (int i = 0; i < objects.size && freeObjects.size >= max; i++) {
			byte[] object = objects.get(i);
			if (object != null) {
				Arrays.fill(object, (byte) 0);
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

	private static class ArrayComparable implements Comparator<byte[]> {
		private static final ArrayComparable instance = new ArrayComparable();

		@Override
		public int compare(byte[] o1, byte[] o2) {
			return Values.compare(o1.length, o2.length);
		}
	}
}
