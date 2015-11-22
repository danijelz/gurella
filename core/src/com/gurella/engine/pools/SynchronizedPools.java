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
	 * Class to {@link ReflectionPool} map. Note the max size is ignored if this
	 * is not the first time this pool has been requested.
	 */
	static public <T> Pool<T> get(Class<T> type, int max) {
		synchronized (type) {
			@SuppressWarnings("unchecked")
			ReflectionPool<T> pool = (ReflectionPool<T>) typePools.get(type);
			if (pool == null) {
				pool = new ReflectionPool<T>(type, 4, max);
				typePools.put(type, pool);
			}
			return pool;
		}
	}

	/**
	 * Returns a new or existing pool for the specified type, stored in a a
	 * Class to {@link ReflectionPool} map. The max size of the pool used is
	 * 100.
	 */
	static public <T> Pool<T> get(Class<T> type) {
		return get(type, 100);
	}

	/** Obtains an object from the {@link #get(Class) pool}. */
	@SuppressWarnings("cast")
	static public <T> T obtain(Class<T> type) {
		synchronized (type) {
			return (T) get(type).obtain();
		}
	}

	/** Frees an object from the {@link #get(Class) pool}. */
	static public <T> void free(T object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null.");
		}

		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>) object.getClass();
		synchronized (type) {
			@SuppressWarnings("unchecked")
			ReflectionPool<T> pool = (ReflectionPool<T>) typePools.get(type);
			if (pool == null) {
				return; // Ignore freeing an object that was never retained.
			}
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
			synchronized (type) {
				if (currentType != type) {
					@SuppressWarnings("unchecked")
					ReflectionPool<Object> casted = (ReflectionPool<Object>) typePools.get(type);
					pool = casted;
					if (pool == null) {
						continue;
					}
				}

				pool.free(object);
			}
		}
	}
}
