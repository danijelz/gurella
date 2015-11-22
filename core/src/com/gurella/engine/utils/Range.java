package com.gurella.engine.utils;

public class Range<T extends Comparable<T>> {
	private T min;
	private T max;

	public Range(T min, T max) {
		this.min = min;
		this.max = max;
	}

	public boolean isInRange(T value) {
		return min.compareTo(value) <= 0 && max.compareTo(value) >= 0;
	}

	public T clamp(T value) {
		return min.compareTo(value) > 0
				? min
				: max.compareTo(value) < 0
						? max
						: value;
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}
}
