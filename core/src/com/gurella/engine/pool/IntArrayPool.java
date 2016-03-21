package com.gurella.engine.pool;

import java.util.Arrays;

public class IntArrayPool extends ArrayPool<int[]> {
	public IntArrayPool() {
		super();
	}

	public IntArrayPool(int initialCapacity) {
		super(initialCapacity);
	}

	public IntArrayPool(int initialCapacity, int max) {
		super(initialCapacity, max);
	}

	@Override
	protected int length(int[] array) {
		return array.length;
	}

	@Override
	protected int[] newObject(int length) {
		return new int[length];
	}

	@Override
	protected void clear(int[] array) {
		Arrays.fill(array, 0);
	}
}
