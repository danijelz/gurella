package com.gurella.engine.pool;

import java.util.Arrays;

public class LongArrayPool extends ArrayPool<long[]> {
	public LongArrayPool() {
		super();
	}

	public LongArrayPool(int initialCapacity) {
		super(initialCapacity);
	}

	public LongArrayPool(int initialCapacity, int max) {
		super(initialCapacity, max);
	}

	@Override
	protected int length(long[] array) {
		return array.length;
	}

	@Override
	protected long[] newObject(int length) {
		return new long[length];
	}

	@Override
	protected void clear(long[] array) {
		Arrays.fill(array, 0);
	}
}
