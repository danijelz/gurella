package com.gurella.engine.pool;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.gurella.engine.utils.Values;

public class ObjectArrayPool<T> implements ArrayPool<T[]> {
	public final Class<T> componentType;
	public final int max;
	private final Array<T[]> freeObjects;

	public ObjectArrayPool(Class<T> componentType) {
		this(componentType, 64, Integer.MAX_VALUE);
	}

	public ObjectArrayPool(Class<T> componentType, int initialCapacity) {
		this(componentType, initialCapacity, Integer.MAX_VALUE);
	}

	public ObjectArrayPool(Class<T> componentType, int initialCapacity, int max) {
		this.componentType = componentType;
		this.max = max;
		freeObjects = new Array<T[]>(initialCapacity);
	}

	@Override
	public T[] obtain(int length, int maxLength) {
		T[] array = find(length, maxLength);
		return array == null ? Values.<T[]> cast(ArrayReflection.newInstance(componentType, length)) : array;
	}

	private T[] find(int length, int maxLength) {
		int low = 0;
		int high = freeObjects.size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			T[] midVal = freeObjects.get(mid);

			if (midVal.length < length) {
				low = mid + 1;
			} else if (midVal.length > length) {
				high = mid - 1;
				if (high >= 0) {
					T[] temp = freeObjects.get(high);
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
	public void free(T[] object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size < max) {
			Arrays.fill(object, null);
			freeObjects.add(object);
			freeObjects.sort(ArrayComparable.<T> getInstance());
		}
	}

	public void freeAll(Array<T[]> objects) {
		if (objects == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (freeObjects.size >= max) {
			return;
		}

		for (int i = 0; i < objects.size && freeObjects.size >= max; i++) {
			T[] object = objects.get(i);
			if (object != null) {
				Arrays.fill(object, null);
				freeObjects.add(object);
			}
		}

		freeObjects.sort(ArrayComparable.<T> getInstance());
	}

	public void clear() {
		freeObjects.clear();
	}

	public int getFree() {
		return freeObjects.size;
	}

	private static class ArrayComparable<T> implements Comparator<T[]> {
		private static final ArrayComparable<Object> instance = new ArrayComparable<Object>();

		public static <T> ArrayComparable<T> getInstance() {
			return Values.cast(instance);
		}

		@Override
		public int compare(T[] o1, T[] o2) {
			return Values.compare(o1.length, o2.length);
		}
	}
}
