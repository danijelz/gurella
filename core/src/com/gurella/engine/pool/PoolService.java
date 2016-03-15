package com.gurella.engine.pool;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * Stores a map of {@link ReflectionPool}s by type for convenient static access.
 * 
 * @author Nathan Sweet
 */
public final class PoolService {
	static private final ObjectMap<Class<?>, ReflectionPool<?>> pools = new ObjectMap<Class<?>, ReflectionPool<?>>();

	private PoolService() {
	}

	private static <T> Pool<T> get(Class<T> type) {
		synchronized (pools) {
			@SuppressWarnings("unchecked")
			ReflectionPool<T> pool = (ReflectionPool<T>) pools.get(type);
			if (pool == null) {
				pool = new ReflectionPool<T>(type, 4, 100);
				pools.put(type, pool);
			}
			return pool;
		}
	}

	@SuppressWarnings("cast")
	public static <T> T obtain(Class<T> type) {
		Pool<T> pool = get(type);
		synchronized (pool) {
			return (T) pool.obtain();
		}
	}

	static <T> T obtain(Class<T> componentType, int length, int maxLength) {
		return ArrayPools.obtain(componentType, length, maxLength);
	}

	public static <T> void free(T object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		if (object.getClass().isArray()) {
			ArrayPools.free(object);
		}

		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>) object.getClass();
		ReflectionPool<T> pool;
		synchronized (pools) {
			@SuppressWarnings("unchecked")
			ReflectionPool<T> casted = (ReflectionPool<T>) pools.get(type);
			pool = casted;
		}

		if (pool == null) {
			// Ignore freeing an object that was never retained.
			return;
		}

		synchronized (pool) {
			pool.free(object);
		}
	}

	public static void freeAll(Array<?> objects) {
		if (objects == null) {
			return;
		}

		ReflectionPool<Object> pool = null;
		Class<?> currentType = null;

		for (int i = 0, n = objects.size; i < n; i++) {
			Object object = objects.get(i);

			if (object == null) {
				continue;
			}

			Class<?> type = object.getClass();
			//TODO arrayPool
			if (currentType != type) {
				synchronized (pools) {
					@SuppressWarnings("unchecked")
					ReflectionPool<Object> casted = (ReflectionPool<Object>) pools.get(type);
					pool = casted;
					if (pool == null) {
						continue;
					}
				}
			}

			synchronized (pool) {
				pool.free(object);
			}
		}
	}
}
