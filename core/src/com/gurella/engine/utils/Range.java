package com.gurella.engine.utils;

public class Range<T extends Comparable<T>> {
	private T min;
	private T max;

	public Range(T min, T max) {
		this.min = min;
		this.max = max;
	}

	public boolean isInRange(T value) {
		return (min == null || min.compareTo(value) <= 0) && (max == null || max.compareTo(value) >= 0);
	}

	public T clamp(T value) {
		return min.compareTo(value) > 0 ? min : max.compareTo(value) < 0 ? max : value;
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((max == null) ? 0 : max.hashCode());
		result = prime * result + ((min == null) ? 0 : min.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Range)) {
			return false;
		}

		Range<?> other = (Range<?>) obj;
		if (max == null) {
			if (other.max != null) {
				return false;
			}
		} else if (!max.equals(other.max)) {
			return false;
		}

		if (min == null) {
			if (other.min != null) {
				return false;
			}
		} else if (!min.equals(other.min)) {
			return false;
		}

		return true;
	}

}
