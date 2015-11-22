package com.gurella.engine.utils;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;

public class IndexedValue<T> {
	private int VALUE_INDEX = 0;
	private ObjectIntMap<T> INDEX_BY_VALUE = new ObjectIntMap<T>();
	private IntMap<T> VALUE_BY_INDEX = new IntMap<T>();

	public synchronized int getIndex(T value) {
		int type = INDEX_BY_VALUE.get(value, -1);

		if (type == -1) {
			type = VALUE_INDEX++;
			INDEX_BY_VALUE.put(value, type);
			VALUE_BY_INDEX.put(type, value);
		}

		return type;
	}
	
	public boolean isIndexed(T value) {
		return INDEX_BY_VALUE.get(value, -1) != -1;
	}
	
	public synchronized T getIndexed(T value) {
		int index = INDEX_BY_VALUE.get(value, -1);
		return index == -1 ? null : getValueByIndex(index);
	}

	public synchronized T getValueByIndex(int index) {
		return VALUE_BY_INDEX.get(index);
	}
	
	public synchronized T removeIndexed(T value) {
		int index = INDEX_BY_VALUE.remove(value, -1);
		return index == -1 ? null : VALUE_BY_INDEX.remove(index);
	}
}
