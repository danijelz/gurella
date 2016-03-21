package com.gurella.engine.pool;

import java.util.Arrays;

public class ShortArrayPool extends ArrayPool<short[]> {
	public ShortArrayPool() {
		super();
	}

	public ShortArrayPool(int initialCapacity) {
		super(initialCapacity);
	}

	public ShortArrayPool(int initialCapacity, int max) {
		super(initialCapacity, max);
	}

	@Override
	protected int length(short[] array) {
		return array.length;
	}

	@Override
	protected short[] newObject(int length) {
		return new short[length];
	}

	@Override
	protected void clear(short[] array) {
		Arrays.fill(array, (short) 0);
	}
}
