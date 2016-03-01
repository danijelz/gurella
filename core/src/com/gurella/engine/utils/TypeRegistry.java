package com.gurella.engine.utils;

import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;

public class TypeRegistry<T> {
	public static final int invalidId = -1;

	private final AtomicInteger sequence = new AtomicInteger(0);
	private final ObjectIntMap<Class<? extends T>> idsByType = new ObjectIntMap<Class<? extends T>>();
	private final IntMap<Class<? extends T>> typesById = new IntMap<Class<? extends T>>();

	public int getId(T value) {
		return getId(Values.<Class<? extends T>> cast(value));
	}

	public int getId(Class<? extends T> valueClass) {
		int id = idsByType.get(valueClass, invalidId);

		if (id == invalidId) {
			id = sequence.getAndIncrement();
			idsByType.put(valueClass, id);
			typesById.put(id, valueClass);
		}

		return id;
	}

	public int findId(Class<? extends T> type) {
		return idsByType.get(type, invalidId);
	}

	public boolean contais(Class<? extends T> type) {
		return idsByType.get(type, invalidId) > invalidId;
	}

	public Class<? extends T> getType(int id) {
		return typesById.get(id);
	}
}
