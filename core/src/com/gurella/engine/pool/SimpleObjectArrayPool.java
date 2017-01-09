package com.gurella.engine.pool;

final class SimpleObjectArrayPool extends ObjectArrayPool<Object> {
	public SimpleObjectArrayPool() {
		super(Object.class);
	}

	@Override
	protected Object[] newObject(int length) {
		return new Object[length];
	}
}