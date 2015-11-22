package com.gurella.engine.utils;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;

public class IndexedType<T> {
	private int TYPE_INDEX = 0;
	private ObjectIntMap<Class<? extends T>> TYPE_BY_CLASS = new ObjectIntMap<Class<? extends T>>();
	private IntMap<Class<? extends T>> CLASS_BY_TYPE = new IntMap<Class<? extends T>>();

	public int getType(T value) {
		@SuppressWarnings("unchecked")
		Class<? extends T> valueClass = (Class<? extends T>) value.getClass();
		return getType(valueClass);
	}

	public int getType(Class<? extends T> valueClass) {
		int type = TYPE_BY_CLASS.get(valueClass, -1);

		if (type == -1) {
			type = TYPE_INDEX++;
			TYPE_BY_CLASS.put(valueClass, type);
			CLASS_BY_TYPE.put(type, valueClass);
		}

		return type;
	}

	public int findType(Class<? extends T> valueClass, int defaultValue) {
		return TYPE_BY_CLASS.get(valueClass, defaultValue);
	}

	public boolean contais(Class<? extends T> valueClass) {
		int type = TYPE_BY_CLASS.get(valueClass, -1);
		return type != -1;
	}

	public Class<? extends T> getClassByType(int type) {
		return CLASS_BY_TYPE.get(type);
	}
}
