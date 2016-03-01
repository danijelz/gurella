package com.gurella.engine.utils;

import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;

public class TypeSequence<T> {
	public static final int invalidId = -1;
	
	private final AtomicInteger sequence = new AtomicInteger(0);
	private final ObjectIntMap<Class<? extends T>> idsByType = new ObjectIntMap<Class<? extends T>>();
	private final IntMap<Class<? extends T>> typesById = new IntMap<Class<? extends T>>();

	public int getType(T value) {
		@SuppressWarnings("unchecked")
		Class<? extends T> valueClass = (Class<? extends T>) value.getClass();
		return getTypeId(valueClass);
	}

	public int getTypeId(Class<? extends T> valueClass) {
		int type = idsByType.get(valueClass, invalidId);

		if (type == invalidId) {
			type = sequence.getAndIncrement();
			idsByType.put(valueClass, type);
			typesById.put(type, valueClass);
		}

		return type;
	}

	public int findTypeId(Class<? extends T> type) {
		return idsByType.get(type, invalidId);
	}

	public boolean contais(Class<? extends T> type) {
		return idsByType.get(type, invalidId) > invalidId;
	}

	public Class<? extends T> getTypeById(int id) {
		return typesById.get(id);
	}
}
