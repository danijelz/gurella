package com.gurella.engine.utils;

import java.util.Map;

public class ValueUtils {
	private ValueUtils() {
	}

	public static boolean isNotEmpty(Object value) {
		return !isEmpty(value);
	}

	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		} else if (value instanceof String) {
			return ((String) value).trim().length() <= 0;
		} else if (value instanceof Map) {
			return ((Map<?, ?>) value).isEmpty();
		} else if (value instanceof Iterable) {
			return !((Iterable<?>) value).iterator().hasNext();
		} else if (value.getClass().isArray()) {
			return ((Object[]) value).length <= 0;
		} else {
			return false;
		}
	}

	public static boolean isEqual(Object first, Object second) {
		if (first == second) {
			return true;
		} else {
			return first == null ? second == null : first.equals(second);
		}
	}
}
