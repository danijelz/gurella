package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.GdxRuntimeException;

public abstract class Attachment<T> {
	T value;

	public Attachment(T value) {
		if (value == null) {
			throw new GdxRuntimeException("Attachment value must be non null.");
		}
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	protected abstract void attach();

	protected abstract void detach();
}
