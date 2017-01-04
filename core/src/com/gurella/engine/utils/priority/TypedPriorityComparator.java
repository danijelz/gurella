package com.gurella.engine.utils.priority;

import java.util.Comparator;

import com.gurella.engine.utils.Values;

public class TypedPriorityComparator implements Comparator<Object> {
	private Class<?> interfaceType;

	public TypedPriorityComparator(Class<?> interfaceType) {
		this.interfaceType = interfaceType;
	}

	@Override
	public int compare(Object o1, Object o2) {
		return Values.compare(PriorityManager.getPriority(o1, interfaceType), PriorityManager.getPriority(o2, interfaceType));
	}
}
