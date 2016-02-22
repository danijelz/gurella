package com.gurella.engine.base.object;

public abstract class ReleasableReference<MO extends ManagedObject, V> {
	MO object;
	V value;

	public ReleasableReference(V value) {
		this.value = value;
	}

	public MO getObject() {
		return object;
	}

	public V getValue() {
		return value;
	}
}
