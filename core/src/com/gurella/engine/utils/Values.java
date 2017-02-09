package com.gurella.engine.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.CharArray;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntFloatMap;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.PooledLinkedList;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.SortedIntList;

public class Values {
	private static final char[] whitespaces = { (char) 0x0009, (char) 0x000A, (char) 0x000B, (char) 0x000C,
			(char) 0x000D, (char) 0x0020, (char) 0x0085, (char) 0x00A0, (char) 0x1680, (char) 0x180E, (char) 0x2000,
			(char) 0x2001, (char) 0x2002, (char) 0x2003, (char) 0x2004, (char) 0x2005, (char) 0x2006, (char) 0x2007,
			(char) 0x2008, (char) 0x2009, (char) 0x200A, (char) 0x200B, (char) 0x200C, (char) 0x200D, (char) 0x2028,
			(char) 0x2029, (char) 0x202F, (char) 0x205F, (char) 0x2060, (char) 0x3000, (char) 0xFEFF };

	private Values() {
	}

	public static boolean isNotEmpty(Object value) {
		return !isEmpty(value);
	}

	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		} else if (value instanceof CharSequence) {
			return ((CharSequence) value).length() <= 0;
		} else if (value instanceof Map) {
			return ((Map<?, ?>) value).isEmpty();
		} else if (value instanceof Collection) {
			return ((Collection<?>) value).isEmpty();
		} else if (value instanceof Array) {
			return ((Array<?>) value).size == 0;
		} else if (value instanceof ArrayMap) {
			return ((ArrayMap<?, ?>) value).size == 0;
		} else if (value instanceof BooleanArray) {
			return ((BooleanArray) value).size == 0;
		} else if (value instanceof ObjectSet) {
			return ((ObjectSet<?>) value).size == 0;
		} else if (value instanceof ObjectMap) {
			return ((ObjectMap<?, ?>) value).size == 0;
		} else if (value instanceof ObjectIntMap) {
			return ((ObjectIntMap<?>) value).size == 0;
		} else if (value instanceof ObjectFloatMap) {
			return ((ObjectFloatMap<?>) value).size == 0;
		} else if (value instanceof LongMap) {
			return ((LongMap<?>) value).size == 0;
		} else if (value instanceof LongArray) {
			return ((LongArray) value).size == 0;
		} else if (value instanceof IntSet) {
			return ((IntSet) value).size == 0;
		} else if (value instanceof IntMap) {
			return ((IntMap<?>) value).size == 0;
		} else if (value instanceof IntIntMap) {
			return ((IntIntMap) value).size == 0;
		} else if (value instanceof IntFloatMap) {
			return ((IntFloatMap) value).size == 0;
		} else if (value instanceof IntArray) {
			return ((IntArray) value).size == 0;
		} else if (value instanceof IdentityMap) {
			return ((IdentityMap<?, ?>) value).size == 0;
		} else if (value instanceof FloatArray) {
			return ((FloatArray) value).size == 0;
		} else if (value instanceof CharArray) {
			return ((CharArray) value).size == 0;
		} else if (value instanceof ByteArray) {
			return ((ByteArray) value).size == 0;
		} else if (value instanceof BooleanArray) {
			return ((BooleanArray) value).size == 0;
		} else if (value instanceof Queue) {
			return ((Queue<?>) value).size == 0;
		} else if (value instanceof ShortArray) {
			return ((ShortArray) value).size == 0;
		} else if (value instanceof SortedIntList) {
			return ((SortedIntList<?>) value).size() == 0;
		} else if (value instanceof PooledLinkedList) {
			return ((PooledLinkedList<?>) value).size() == 0;
		} else if (value instanceof ImmutableArray) {
			return ((ImmutableArray<?>) value).size() == 0;
		} else if (value instanceof Container) {
			return ((Container) value).size() == 0;
		} else if (value instanceof Iterable) {
			return !((Iterable<?>) value).iterator().hasNext();
		} else if (value.getClass().isArray()) {
			return isEmptyArray(value);
		} else {
			return false;
		}
	}

	public static boolean isEmptyArray(Object value) {
		if (value == null) {
			return true;
		} else if (value instanceof long[]) {
			return ((long[]) value).length <= 0;
		} else if (value instanceof int[]) {
			return ((int[]) value).length <= 0;
		} else if (value instanceof short[]) {
			return ((short[]) value).length <= 0;
		} else if (value instanceof char[]) {
			return ((char[]) value).length <= 0;
		} else if (value instanceof byte[]) {
			return ((byte[]) value).length <= 0;
		} else if (value instanceof double[]) {
			return ((double[]) value).length <= 0;
		} else if (value instanceof float[]) {
			return ((float[]) value).length <= 0;
		} else if (value instanceof boolean[]) {
			return ((boolean[]) value).length <= 0;
		} else {
			return ((Object[]) value).length <= 0;
		}
	}

	public static boolean isNotBlank(final CharSequence sequence) {
		return !isBlank(sequence);
	}

	public static boolean isBlank(final CharSequence sequence) {
		if (sequence == null) {
			return true;
		}

		int length = sequence.length();
		int index = 0;

		while ((index < length) && (sequence.charAt(index) == ' ')) {
			index++;
		}

		return index == length;
	}

	public static boolean nullSafeEquals(Object first, Object second) {
		return first == second || (first != null && first.equals(second));
	}

	public static boolean isEqual(Object first, Object second) {
		return isEqual(first, second, true);
	}

	public static boolean isNotEqual(Object first, Object second) {
		return !isEqual(first, second, true);
	}

	//TODO rename to deepEquals ??
	public static boolean isEqual(Object first, Object second, boolean mustEqualClass) {
		if (first == second) {
			return true;
		} else if (first == null || second == null) {
			return false;
		}

		Class<?> firstType = first.getClass();
		Class<?> secondType = second.getClass();
		boolean array = firstType.isArray();

		if ((mustEqualClass && firstType != secondType) || (array != secondType.isArray())) {
			return false;
		} else if (array) {
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
			} else if (first instanceof Equality) {
				return ((Equality) first).equalAs(second);
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

	public static boolean isNotEqual(Object first, Object second, boolean mustEqualClass) {
		return !isEqual(first, second, mustEqualClass);
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object object) {
		return (T) object;
	}

	public static int compare(int x, int y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	public static int compare(long x, long y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	public static String format(final String format, final String... args) {
		String[] split = format.split("%s");
		final StringBuilder msg = new StringBuilder();
		for (int pos = 0; pos < split.length - 1; pos++) {
			msg.append(split[pos]);
			msg.append(args[pos]);
		}
		msg.append(split[split.length - 1]);
		return msg.toString();
	}

	public static boolean isWhitespace(char c) {
		return Arrays.binarySearch(whitespaces, c) > -1;
	}
}
