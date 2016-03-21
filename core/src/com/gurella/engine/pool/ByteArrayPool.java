package com.gurella.engine.pool;

import java.util.Arrays;

public class ByteArrayPool extends ArrayPool<byte[]> {
	public ByteArrayPool() {
		super();
	}

	public ByteArrayPool(int initialCapacity) {
		super(initialCapacity);
	}

	public ByteArrayPool(int initialCapacity, int max) {
		super(initialCapacity, max);
	}

	@Override
	protected int length(byte[] array) {
		return array.length;
	}

	@Override
	protected byte[] newObject(int length) {
		return new byte[length];
	}

	@Override
	protected void clear(byte[] array) {
		Arrays.fill(array, (byte) 0);
	}
}
