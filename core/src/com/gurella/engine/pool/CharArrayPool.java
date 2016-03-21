package com.gurella.engine.pool;

import java.util.Arrays;

public class CharArrayPool extends ArrayPool<char[]> {
	public CharArrayPool() {
		super();
	}

	public CharArrayPool(int initialCapacity) {
		super(initialCapacity);
	}

	public CharArrayPool(int initialCapacity, int max) {
		super(initialCapacity, max);
	}

	@Override
	protected int length(char[] array) {
		return array.length;
	}

	@Override
	protected char[] newObject(int length) {
		return new char[length];
	}

	@Override
	protected void clear(char[] array) {
		Arrays.fill(array, (char) 0);
	}
}
