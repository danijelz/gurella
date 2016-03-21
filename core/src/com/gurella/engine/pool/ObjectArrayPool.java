package com.gurella.engine.pool;

import java.util.Arrays;

import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.gurella.engine.utils.Values;

public class ObjectArrayPool<T> extends ArrayPool<T[]> {
	public final Class<T> componentType;

	public ObjectArrayPool(Class<T> componentType) {
		this(componentType, 64, Integer.MAX_VALUE);
	}

	public ObjectArrayPool(Class<T> componentType, int initialCapacity) {
		this(componentType, initialCapacity, Integer.MAX_VALUE);
	}

	public ObjectArrayPool(Class<T> componentType, int initialCapacity, int max) {
		super(initialCapacity, max);
		this.componentType = componentType;
	}

	@Override
	protected int length(T[] array) {
		return array.length;
	}

	@Override
	protected T[] newObject(int length) {
		return Values.<T[]> cast(ArrayReflection.newInstance(componentType, length));
	}

	@Override
	protected void clear(T[] array) {
		Arrays.fill(array, null);
	}
}
