package com.gurella.engine.utils;

import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

//TODO Poolable
public class ArrayExt<T> extends Array<T> implements Poolable, Container {
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
	// TODO is pool service needed
	public <V> V[] toArray(@SuppressWarnings("rawtypes") Class type) {
		V[] result = PoolService.obtainArray(Values.<Class<V>> cast(type), size, size);
		System.arraycopy(items, 0, result, 0, size);
		return result;
	}

	@Override
	public T[] shrink() {
		if (items.length != size) {
			resize(size, 0);
		}
		return items;
	}

	@Override
	protected T[] resize(int newSize) {
		return resize(newSize, 0.3f);
	}

	protected T[] resize(int newSize, float maxDeviation) {
		T[] items = this.items;
		Class<T> type = Values.<Class<T>> cast(items.getClass().getComponentType());
		T[] newItems = PoolService.obtainArray(type, newSize, maxDeviation);
		System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
		PoolService.free(items);
		this.items = newItems;
		return newItems;
	}

	public void addAll(ImmutableArray<? extends T> array) {
		array.appendTo(this);
	}

	public void addAll(ImmutableArray<? extends T> array, int start, int count) {
		array.appendTo(this, start, count);
	}

	public void setIndex(int newIndex, T value, boolean identity) {
		int oldIndex = indexOf(value, identity);
		if (oldIndex == newIndex) {
			return;
		}

		T[] items = this.items;

		if (oldIndex < newIndex) {
			System.arraycopy(items, oldIndex + 1, items, oldIndex, newIndex - oldIndex);
		} else {
			System.arraycopy(items, newIndex, items, newIndex + 1, oldIndex - newIndex);
		}

		items[newIndex] = value;
	}

	@Override
	public int size() {
		return size;
	}

	public ImmutableArray<T> immutable() {
		if (immutable == null) {
			immutable = new ImmutableArray<T>(this);
		}
		return immutable;
	}

	@Override
	public void reset() {
		if (items.length > 32) {
			resize(16, 1);
		} else {
			Arrays.fill(items, null);
		}
		ordered = true;
		size = 0;
	}

	@Override
	public int hashCode() {
		return 31 + super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ArrayExt)) {
			return false;
		}
		return super.equals(obj);
	}

	static public <T> ArrayExt<T> with(T... array) {
		return new ArrayExt<T>(array);
	}
}
