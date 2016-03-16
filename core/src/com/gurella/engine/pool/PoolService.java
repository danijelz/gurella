package com.gurella.engine.pool;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.gurella.engine.factory.Factories;
import com.gurella.engine.factory.Factory;

/**
 * Stores a map of {@link ReflectionPool}s by type for convenient static access. TODO factory pools
 * 
 * @author Nathan Sweet
 */
public final class PoolService {
	static private final ObjectMap<Class<?>, Pool<?>> pools = new ObjectMap<Class<?>, Pool<?>>();

	private PoolService() {
	}

	private static <T> Pool<T> getPool(Class<T> type) {
		synchronized (pools) {
			@SuppressWarnings("unchecked")
			Pool<T> pool = (Pool<T>) pools.get(type);
			if (pool == null) {
				Factory<T> factory = Factories.getFactory(type);
				pool = factory == null ? new ReflectionPool<T>(type) : new FactoryPool<T>(factory);
				pools.put(type, pool);
			}
			return pool;
		}
	}

	public static <T> void setPool(Class<T> type, Pool<T> pool) {
		synchronized (pools) {
			pools.put(type, pool);
		}
	}

	public static <T> T obtain(Class<T> type) {
		Pool<T> pool = getPool(type);
		synchronized (pool) {
			return pool.obtain();
		}
	}

	public static <T> T obtain(Class<?> componentType, int length, float maxDeviation) {
		// TODO sync
		return ArrayPools.obtain(componentType, length, maxDeviation);
	}

	public static <T> T obtain(Class<?> componentType, int length, int maxLength) {
		// TODO sync
		return ArrayPools.obtain(componentType, length, maxLength);
	}

	public static <T> void free(T object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		// TODO do in background thread

		if (object.getClass().isArray()) {
			// TODO sync
			ArrayPools.free(object);
			return;
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

		// TODO array pool

		ReflectionPool<Object> pool = null;
		Class<?> currentType = null;

		for (int i = 0, n = objects.size; i < n; i++) {
			Object object = objects.get(i);

			if (object == null) {
				continue;
			}

			Class<?> type = object.getClass();
			// TODO arrayPool
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
