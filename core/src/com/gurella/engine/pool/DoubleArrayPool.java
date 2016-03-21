package com.gurella.engine.pool;

import java.util.Arrays;

public class DoubleArrayPool extends ArrayPool<double[]> {
	public DoubleArrayPool() {
		super();
	}

	public DoubleArrayPool(int initialCapacity) {
		super(initialCapacity);
	}

	public DoubleArrayPool(int initialCapacity, int max) {
		super(initialCapacity, max);
	}

	@Override
	protected int length(double[] array) {
		return array.length;
	}

	@Override
	protected double[] newObject(int length) {
		return new double[length];
	}

	@Override
	protected void clear(double[] array) {
		Arrays.fill(array, 0);
	}
}
