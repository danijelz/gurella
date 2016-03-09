package com.gurella.engine.utils;

import com.badlogic.gdx.utils.Array;

public class ArrayExt<T> extends Array<T> {
	private ImmutableArray<T> immutable;

	public ArrayExt() {
		super();
	}

	public ArrayExt(Array<? extends T> array) {
		super(array);
	}

	public ArrayExt(boolean ordered, int capacity, Class<T> arrayType) {
		super(ordered, capacity, arrayType);
	}

	public ArrayExt(boolean ordered, int capacity) {
		super(ordered, capacity);
	}

	public ArrayExt(boolean ordered, T[] array, int start, int count) {
		super(ordered, array, start, count);
	}

	public ArrayExt(Class<T> arrayType) {
		super(arrayType);
	}

	public ArrayExt(int capacity) {
		super(capacity);
	}

	public ArrayExt(T[] array) {
		super(array);
	}
	
	@Override
	public T[] toArray() {
		// TODO array pool
		return super.toArray();
	}
	
	@Override
	protected T[] resize(int newSize) {
		// TODO array pool
		return super.resize(newSize);
	}

	public ImmutableArray<T> immutable() {
		if (immutable == null) {
			immutable = new ImmutableArray<T>(this);
		}
		return immutable;
	}

	static public <T> ArrayExt<T> with(T... array) {
		return new ArrayExt<T>(array);
	}
}
