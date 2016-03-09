package com.gurella.engine.pool;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.PooledLinkedList;

//TODO unused
//https://bitbucket.org/dermetfan/libgdx-utils/src/d76d371462008fdda7252ec266130a03b226f9d1/src/main/net/dermetfan/gdx/utils/ArrayPool.java?at=default&fileviewer=file-view-default
public abstract class ArrayPool<T> {
	private final IntMap<PooledLinkedList<T[]>> lists;
	private final Pool<PooledLinkedList<T[]>> listPool;

	/** the maximum amount of arrays of different lengths that will be pooled */
	public final int max;

	/** the maximum amount of arrays of the same length that will be pooled */
	public final int maxEach;

	public ArrayPool(int max, final int maxEach) {
		this.max = max;
		this.maxEach = maxEach;

		lists = new IntMap<PooledLinkedList<T[]>>(max < 0 ? 10 : max, 1);
		listPool = new Pool<PooledLinkedList<T[]>>() {
			@Override
			protected PooledLinkedList<T[]> newObject() {
				return new PooledLinkedList<>(maxEach);
			}
		};
	}

	/** @return a new array of the given length */
	protected abstract T[] newArray(int length);

	/**
	 * @param length the desired length of the array
	 */
	public T[] obtain(int length) {
		if (length < 0) {
			throw new IllegalArgumentException("negative array length: " + length);
		}
		PooledLinkedList<T[]> list = lists.get(length);
		if (list == null) {
			return newArray(length);
		}
		list.iterReverse();
		T[] array = list.previous();
		list.remove();
		if (list.previous() == null) {
			lists.remove(length);
			listPool.free(list);
		}
		return array;
	}

	/**
	 * @param array the array to put back into the pool
	 */
	public void free(T[] array) {
		if (array == null)
			throw new IllegalArgumentException("array cannot be null");
		PooledLinkedList<T[]> list = lists.get(array.length);
		if (list == null && lists.size < max) {
			list = listPool.obtain();
			lists.put(array.length, list);
		}
		if (list != null && size(list) < maxEach) {
			list.add(array);
		}
	}

	public void clear() {
		for (PooledLinkedList<T[]> list : lists.values()) {
			list.clear();
		}
		lists.clear();
	}

	/** @return the number of arrays of the given length in the pool */
	public int getFree(int length) {
		PooledLinkedList<T[]> list = lists.get(length);
		return list == null ? 0 : size(list);
	}

	private int size(PooledLinkedList list) {
		int size = 0;
		list.iter();
		while (list.next() != null) {
			size++;
		}
		return size;
	}
}