package com.gurella.engine.pool;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.Values;

final class ArrayPools {
	private static final ObjectMap<Class<?>, ObjectArrayPool<?>> objectPools = new ObjectMap<Class<?>, ObjectArrayPool<?>>();
	private static final BooleanArrayPool booleanArrayPool = new BooleanArrayPool();
	private static final ByteArrayPool byteArrayPool = new ByteArrayPool();
	private static final CharArrayPool charArrayPool = new CharArrayPool();
	private static final ShortArrayPool shortArrayPool = new ShortArrayPool();
	private static final IntArrayPool intArrayPool = new IntArrayPool();
	private static final LongArrayPool longArrayPool = new LongArrayPool();
	private static final FloatArrayPool floatArrayPool = new FloatArrayPool();
	private static final DoubleArrayPool doubleArrayPool = new DoubleArrayPool();

	private ArrayPools() {
	}

	static <T> T obtain(Class<?> componentType, int length, float maxDeviation) {
		return obtain(componentType, length, length + (int) (length * maxDeviation));
	}

	static <T> T obtain(Class<?> componentType, int length, int maxLength) {
		if (componentType.isPrimitive()) {
			if (boolean.class == componentType) {
				return Values.cast(booleanArrayPool.obtain(length, maxLength));
			} else if (byte.class == componentType) {
				return Values.cast(byteArrayPool.obtain(length, maxLength));
			} else if (char.class == componentType) {
				return Values.cast(charArrayPool.obtain(length, maxLength));
			} else if (short.class == componentType) {
				return Values.cast(shortArrayPool.obtain(length, maxLength));
			} else if (int.class == componentType) {
				return Values.cast(intArrayPool.obtain(length, maxLength));
			} else if (long.class == componentType) {
				return Values.cast(longArrayPool.obtain(length, maxLength));
			} else if (float.class == componentType) {
				return Values.cast(floatArrayPool.obtain(length, maxLength));
			} else if (double.class == componentType) {
				return Values.cast(doubleArrayPool.obtain(length, maxLength));
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			@SuppressWarnings("unchecked")
			ObjectArrayPool<Object> pool = (ObjectArrayPool<Object>) objectPools.get(componentType);
			if (pool == null) {
				pool = new ObjectArrayPool<Object>(Values.cast(componentType));
				objectPools.put(componentType, pool);
			}
			return Values.cast(pool.obtain(length, maxLength));
		}
	}

	static void free(Object object) {
		Class<?> componentType = object.getClass().getComponentType();
		if (componentType.isPrimitive()) {
			if (boolean.class == componentType) {
				booleanArrayPool.free(Values.<boolean[]> cast(object));
			} else if (byte.class == componentType) {
				byteArrayPool.free(Values.<byte[]> cast(object));
			} else if (char.class == componentType) {
				charArrayPool.free(Values.<char[]> cast(object));
			} else if (short.class == componentType) {
				shortArrayPool.free(Values.<short[]> cast(object));
			} else if (int.class == componentType) {
				intArrayPool.free(Values.<int[]> cast(object));
			} else if (long.class == componentType) {
				longArrayPool.free(Values.<long[]> cast(object));
			} else if (float.class == componentType) {
				floatArrayPool.free(Values.<float[]> cast(object));
			} else if (double.class == componentType) {
				doubleArrayPool.free(Values.<double[]> cast(object));
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			ObjectArrayPool<Object> pool = Values.cast(objectPools.get(componentType));
			if (pool == null) {
				pool = new ObjectArrayPool<Object>(Values.<Class<Object>> cast(componentType));
				objectPools.put(componentType, pool);
			}
			pool.free(Values.<Object[]> cast(object));
		}
	}
}