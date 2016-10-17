package com.gurella.engine.utils;

import com.gurella.engine.base.model.ValueRange;
import com.gurella.engine.base.model.ValueRange.ByteRange;
import com.gurella.engine.base.model.ValueRange.CharRange;
import com.gurella.engine.base.model.ValueRange.DoubleRange;
import com.gurella.engine.base.model.ValueRange.FloatRange;
import com.gurella.engine.base.model.ValueRange.IntegerRange;
import com.gurella.engine.base.model.ValueRange.LongRange;
import com.gurella.engine.base.model.ValueRange.ShortRange;

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

	public static Range<?> valueOf(ValueRange valueRange, Class<?> type) {
		if (valueRange == null || type == null) {
			return null;
		}

		if (Integer.class == type || int.class == type || Integer[].class == type || int[].class == type) {
			IntegerRange integerRange = valueRange.integerRange();
			return integerRange == null ? null
					: new Range<Integer>(Integer.valueOf(integerRange.min()), Integer.valueOf(integerRange.max()));
		} else if (Float.class == type || float.class == type || Float[].class == type || float[].class == type) {
			FloatRange floatRange = valueRange.floatRange();
			return floatRange == null ? null
					: new Range<Float>(Float.valueOf(floatRange.min()), Float.valueOf(floatRange.max()));
		} else if (Long.class == type || long.class == type || Long[].class == type || long[].class == type) {
			LongRange longRange = valueRange.longRange();
			return longRange == null ? null
					: new Range<Long>(Long.valueOf(longRange.min()), Long.valueOf(longRange.max()));
		} else if (Double.class == type || double.class == type || Double[].class == type || double[].class == type) {
			DoubleRange doubleRange = valueRange.doubleRange();
			return doubleRange == null ? null
					: new Range<Double>(Double.valueOf(doubleRange.min()), Double.valueOf(doubleRange.max()));
		} else if (Short.class == type || short.class == type || Short[].class == type || short[].class == type) {
			ShortRange shortRange = valueRange.shortRange();
			return shortRange == null ? null
					: new Range<Short>(Short.valueOf(shortRange.min()), Short.valueOf(shortRange.max()));
		} else if (Byte.class == type || byte.class == type || Byte[].class == type || byte[].class == type) {
			ByteRange byteRange = valueRange.byteRange();
			return byteRange == null ? null
					: new Range<Byte>(Byte.valueOf(byteRange.min()), Byte.valueOf(byteRange.max()));
		} else if (Character.class == type || char.class == type || Character[].class == type || char[].class == type) {
			CharRange charRange = valueRange.charRange();
			return charRange == null ? null
					: new Range<Character>(Character.valueOf(charRange.min()), Character.valueOf(charRange.max()));
		} else {
			return null;
		}
	}
}
