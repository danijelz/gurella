package com.gurella.engine.pool;

import com.badlogic.gdx.utils.Pool;
import com.gurella.engine.factory.Factory;

public class FactoryPool<T> extends Pool<T> {
	private final Factory<T> factory;

	public FactoryPool(Factory<T> factory) {
		this(factory, 16, Integer.MAX_VALUE);
	}

	public FactoryPool(Factory<T> factory, int initialCapacity) {
		this(factory, initialCapacity, Integer.MAX_VALUE);
	}

	public FactoryPool(Factory<T> factory, int initialCapacity, int max) {
		super(initialCapacity, max);
		if (factory == null) {
			throw new NullPointerException("Class cannot be created (missing factory).");
		}
		this.factory = factory;
	}

	@Override
	protected T newObject() {
		return factory.create();
	}
}
