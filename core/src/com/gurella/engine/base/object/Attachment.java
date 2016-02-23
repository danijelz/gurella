package com.gurella.engine.base.object;

public abstract class Attachment<MO extends ManagedObject, V> {
	MO object;
	V value;

	public Attachment(V value) {
		this.value = value;
	}

	public MO getObject() {
		return object;
	}

	public V getValue() {
		return value;
	}
	
	protected abstract void attach();

	protected abstract void detach();
}
