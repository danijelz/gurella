package com.gurella.engine.utils;

import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;

public class ValueSequence<T> {
	public static final int invalidId = -1;

	private final AtomicInteger sequence = new AtomicInteger(0);
	private final ObjectIntMap<T> idsByValue = new ObjectIntMap<T>();
	private final IntMap<T> valuesById = new IntMap<T>();

	public synchronized int getId(T value) {
		int type = idsByValue.get(value, invalidId);

		if (type == invalidId) {
			type = sequence.getAndIncrement();
			idsByValue.put(value, type);
			valuesById.put(type, value);
		}

		return type;
	}

	public boolean contains(T value) {
		return idsByValue.get(value, invalidId) != invalidId;
	}

	public synchronized T getValueById(int id) {
		return valuesById.get(id);
	}

	public synchronized T remove(T value) {
		int id = idsByValue.remove(value, invalidId);
		return id == invalidId ? null : valuesById.remove(id);
	}
}
