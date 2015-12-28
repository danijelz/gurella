package com.gurella.engine.pools;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * Stores a map of {@link ReflectionPool}s by type for convenient static access.
 * 
 * @author Nathan Sweet
 */
public class SynchronizedPools {
	static private final ObjectMap<Class<?>, ReflectionPool<?>> typePools = new ObjectMap<Class<?>, ReflectionPool<?>>();

	private SynchronizedPools() {
	}

	/**
	 * Returns a new or existing pool for the specified type, stored in a a
	 * Class to {@link ReflectionPool} map. The max size of the pool used is
	 * 100.
	 */
	static private <T> Pool<T> get(Class<T> type) {
		synchronized (typePools) {
			@SuppressWarnings("unchecked")
			ReflectionPool<T> pool = (ReflectionPool<T>) typePools.get(type);
			if (pool == null) {
				pool = new ReflectionPool<T>(type, 4, 100);
				typePools.put(type, pool);
			}
			return pool;
		}
	}

	/** Obtains an object from the {@link #get(Class) pool}. */
	@SuppressWarnings("cast")
	static public <T> T obtain(Class<T> type) {
		Pool<T> pool = get(type);
		synchronized (pool) {
			return (T) pool.obtain();
		}
	}

	/** Frees an object from the {@link #get(Class) pool}. */
	static public <T> void free(T object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>) object.getClass();
		ReflectionPool<T> pool;
		synchronized (typePools) {
			@SuppressWarnings("unchecked")
			ReflectionPool<T> casted = (ReflectionPool<T>) typePools.get(type);
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

	/**
	 * Frees the specified objects from the {@link #get(Class) pool}. Null
	 * objects within the array are silently ignored. Objects don't need to be
	 * from the same pool.
	 */
	static public void freeAll(Array<?> objects) {
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
			if (currentType != type) {
				synchronized (typePools) {
					@SuppressWarnings("unchecked")
					ReflectionPool<Object> casted = (ReflectionPool<Object>) typePools.get(type);
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
