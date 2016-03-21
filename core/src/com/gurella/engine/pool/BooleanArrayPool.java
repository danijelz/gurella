package com.gurella.engine.pool;

import java.util.Arrays;

public class BooleanArrayPool extends ArrayPool<boolean[]> {
	public BooleanArrayPool() {
		super();
	}

	public BooleanArrayPool(int initialCapacity) {
		super(initialCapacity);
	}

	public BooleanArrayPool(int initialCapacity, int max) {
		super(initialCapacity, max);
	}

	@Override
	protected int length(boolean[] array) {
		return array.length;
	}

	@Override
	protected boolean[] newObject(int length) {
		return new boolean[length];
	}

	@Override
	protected void clear(boolean[] array) {
		Arrays.fill(array, false);
	}
}
