package com.gurella.engine.pool;

import java.util.Arrays;

public class FloatArrayPool extends ArrayPool<float[]> {
	public FloatArrayPool() {
		super();
	}

	public FloatArrayPool(int initialCapacity) {
		super(initialCapacity);
	}

	public FloatArrayPool(int initialCapacity, int max) {
		super(initialCapacity, max);
	}

	@Override
	protected int length(float[] array) {
		return array.length;
	}

	@Override
	protected float[] newObject(int length) {
		return new float[length];
	}

	@Override
	protected void clear(float[] array) {
		Arrays.fill(array, 0);
	}
}
