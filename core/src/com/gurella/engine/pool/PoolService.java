package com.gurella.engine.pool;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.TypePriorities;
import com.gurella.engine.event.TypePriority;
import com.gurella.engine.factory.Factories;
import com.gurella.engine.factory.Factory;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.Values;

/**
 * Stores a map of {@link ReflectionPool}s by type for convenient static access. TODO factory pools
 * 
 * @author Nathan Sweet
 */
@TypePriorities({ @TypePriority(priority = CommonUpdatePriority.CLEANUP, type = ApplicationUpdateListener.class) })
public final class PoolService {
	private static final ObjectMap<Class<?>, Pool<?>> pools = new ObjectMap<Class<?>, Pool<?>>();

	private static final AsyncExecutor executor = DisposablesService.add(new AsyncExecutor(1));
	private static final CleanupTask cleanupTask = new CleanupTask();

	private static ArrayExt<Object> asyncPool = new ArrayExt<Object>(64);

	static {
		EventService.subscribe(cleanupTask);
	}

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
		asyncPool.add(object);
	}

	public static void freeAll(Array<?> objects) {
		asyncPool.addAll(objects);
	}

	private static void freeAsync(Array<?> objects) {
		if (objects == null) {
			return;
		}

		int i = 0;
		Pool<Object> pool = null;
		Class<?> currentType = null;

		while (i < objects.size) {
			Object object = objects.get(i++);
			if (object == null) {
				continue;
			}

			Class<?> type = object.getClass();
			if (type.isArray()) {
				// TODO sync
				ArrayPools.free(object);
			} else {
				if (currentType != type) {
					currentType = type;
					synchronized (pools) {
						pool = Values.cast(pools.get(type));
					}
				}

				if (pool != null) {
					synchronized (pool) {
						pool.free(object);
					}
				}
			}
		}
	}

	private static class CleanupTask implements AsyncTask<Void>, ApplicationUpdateListener {
		static boolean running;
		static ArrayExt<Object> current = new ArrayExt<Object>(64);

		@Override
		public Void call() throws Exception {
			PoolService.freeAsync(current);
			running = false;
			return null;
		}

		@Override
		public void update() {
			if (!running && asyncPool.size > 0) {
				running = true;
				ArrayExt<Object> temp = PoolService.asyncPool;
				PoolService.asyncPool = current;
				current = temp;
				executor.submit(this);
			}
		}
	}
}
