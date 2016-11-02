package com.gurella.engine.utils.priority;

import java.util.Comparator;

import com.gurella.engine.utils.Values;

public class PriorityComparator implements Comparator<Object> {
	public static final PriorityComparator instance = new PriorityComparator();

	private PriorityComparator() {
	}

	@Override
	public int compare(Object o1, Object o2) {
		return Values.compare(Priorities.getPriority(o1), Priorities.getPriority(o2));
	}
}
