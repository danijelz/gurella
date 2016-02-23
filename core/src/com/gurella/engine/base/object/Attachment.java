package com.gurella.engine.base.object;

public abstract class Attachment<T> {
	T value;

	public Attachment(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	protected abstract void attach();

	protected abstract void detach();
}
