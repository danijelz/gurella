package com.gurella.engine.utils;

import java.util.Arrays;
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
		} else if (first == null || second == null) {
			return false;
		}

		Class<?> firstType = first.getClass();
		Class<?> secondType = second.getClass();
		if (firstType != secondType) {
			return false;
		} else if (firstType.isArray()) {
			if (first instanceof long[]) {
				return Arrays.equals((long[]) first, (long[]) second);
			} else if (first instanceof int[]) {
				return Arrays.equals((int[]) first, (int[]) second);
			} else if (first instanceof short[]) {
				return Arrays.equals((short[]) first, (short[]) second);
			} else if (first instanceof char[]) {
				return Arrays.equals((char[]) first, (char[]) second);
			} else if (first instanceof byte[]) {
				return Arrays.equals((byte[]) first, (byte[]) second);
			} else if (first instanceof double[]) {
				return Arrays.equals((double[]) first, (double[]) second);
			} else if (first instanceof float[]) {
				return Arrays.equals((float[]) first, (float[]) second);
			} else if (first instanceof boolean[]) {
				return Arrays.equals((boolean[]) first, (boolean[]) second);
			} else {
				Object[] firstArray = (Object[]) first;
				Object[] secondArray = (Object[]) second;
				if (firstArray.length != secondArray.length) {
					return false;
				}

				for (int i = 0; i < firstArray.length; ++i) {
					if (!isEqual(firstArray[i], secondArray[i])) {
						return false;
					}
				}

				return true;
			}
		} else {
			return first.equals(second);
		}
	}
}
