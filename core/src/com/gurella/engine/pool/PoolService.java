package com.gurella.engine.pool;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.async.AsyncService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.TypePriority;
import com.gurella.engine.factory.Factories;
import com.gurella.engine.factory.Factory;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.Values;

//TODO factory pools, Disposable
@TypePriority(priority = CommonUpdatePriority.cleanupPriority, type = ApplicationUpdateListener.class)
public final class PoolService implements AsyncTask<Void>, ApplicationUpdateListener {
	private static final PoolService instance = new PoolService();
	private static final FreeObjectsComparator comparatorInstance = new FreeObjectsComparator();

	private static final ObjectMap<Class<?>, Pool<?>> pools = new ObjectMap<Class<?>, Pool<?>>();
	private static final ObjectMap<Class<?>, ArrayPool<?>> arrayPools = new ObjectMap<Class<?>, ArrayPool<?>>();

	private static boolean cleaning;
	private static ArrayExt<Object> asyncPool = new ArrayExt<Object>(128);
	private static ArrayExt<Object> cleaningObjects = new ArrayExt<Object>(128);
	private static final Sort sort = new Sort();

	static {
		arrayPools.put(boolean.class, new BooleanArrayPool());
		arrayPools.put(byte.class, new ByteArrayPool());
		arrayPools.put(char.class, new CharArrayPool());
		arrayPools.put(short.class, new ShortArrayPool());
		arrayPools.put(int.class, new IntArrayPool());
		arrayPools.put(long.class, new LongArrayPool());
		arrayPools.put(float.class, new FloatArrayPool());
		arrayPools.put(double.class, new DoubleArrayPool());
		arrayPools.put(Object.class, new ObjectArrayPool<Object>(Object.class) {
			@Override
			protected Object[] newObject(int length) {
				return new Object[length];
			}
		});

		EventService.subscribe(instance);
	}

	private PoolService() {
	}

	private static <T> Pool<T> getObjectPool(Class<T> type) {
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

	private static <T> ArrayPool<T> getArrayPool(Class<?> componentType) {
		synchronized (arrayPools) {
			@SuppressWarnings("unchecked")
			ArrayPool<T> pool = (ArrayPool<T>) arrayPools.get(componentType);
			if (pool == null) {
				pool = Values.cast(new ObjectArrayPool<Object>(Values.<Class<Object>> cast(componentType)));
				arrayPools.put(componentType, pool);
			}
			return pool;
		}
	}

	public static <T> T obtain(Class<T> type) {
		Pool<T> pool = getObjectPool(type);
		synchronized (pool) {
			return pool.obtain();
		}
	}

	public static boolean[] obtainBooleanArray(int length, float maxDeviation) {
		return obtainArrayInternal(boolean.class, length, length + (int) (length * maxDeviation));
	}

	public static boolean[] obtainBooleanArray(int length, int maxLength) {
		return obtainArrayInternal(boolean.class, length, maxLength);
	}

	public static byte[] obtainByteArray(int length, float maxDeviation) {
		return obtainArrayInternal(byte.class, length, length + (int) (length * maxDeviation));
	}

	public static byte[] obtainByteArray(int length, int maxLength) {
		return obtainArrayInternal(byte.class, length, maxLength);
	}

	public static char[] obtainCharArray(int length, float maxDeviation) {
		return obtainArrayInternal(char.class, length, length + (int) (length * maxDeviation));
	}

	public static char[] obtainCharArray(int length, int maxLength) {
		return obtainArrayInternal(char.class, length, maxLength);
	}

	public static short[] obtainShortArray(int length, float maxDeviation) {
		return obtainArrayInternal(short.class, length, length + (int) (length * maxDeviation));
	}

	public static short[] obtainShortArray(int length, int maxLength) {
		return obtainArrayInternal(short.class, length, maxLength);
	}

	public static int[] obtainIntArray(int length, float maxDeviation) {
		return obtainArrayInternal(int.class, length, length + (int) (length * maxDeviation));
	}

	public static int[] obtainIntArray(int length, int maxLength) {
		return obtainArrayInternal(int.class, length, maxLength);
	}

	public static long[] obtainLongArray(int length, float maxDeviation) {
		return obtainArrayInternal(long.class, length, length + (int) (length * maxDeviation));
	}

	public static long[] obtainLongArray(int length, int maxLength) {
		return obtainArrayInternal(long.class, length, maxLength);
	}

	public static float[] obtainFloatArray(int length, float maxDeviation) {
		return obtainArrayInternal(float.class, length, length + (int) (length * maxDeviation));
	}

	public static float[] obtainFloatArray(int length, int maxLength) {
		return obtainArrayInternal(float.class, length, maxLength);
	}

	public static double[] obtainDoubleArray(int length, float maxDeviation) {
		return obtainArrayInternal(double.class, length, length + (int) (length * maxDeviation));
	}

	public static double[] obtainDoubleArray(int length, int maxLength) {
		return obtainArrayInternal(double.class, length, maxLength);
	}

	public static <T> T[] obtainArray(Class<T> componentType, int length, float maxDeviation) {
		return obtainArrayInternal(componentType, length, length + (int) (length * maxDeviation));
	}

	public static <T> T[] obtainArray(Class<T> componentType, int length, int maxLength) {
		return obtainArrayInternal(componentType, length, maxLength);
	}

	private static <T> T obtainArrayInternal(Class<?> componentType, int length, int maxLength) {
		@SuppressWarnings("unchecked")
		ArrayPool<T> pool = (ArrayPool<T>) getArrayPool(componentType);
		synchronized (pool) {
			return pool.obtain(length, maxLength);
		}
	}

	public static <T> void free(T object) {
		synchronized (asyncPool) {
			asyncPool.add(object);
		}
	}

	public static void freeAll(Object... objects) {
		synchronized (asyncPool) {
			asyncPool.addAll(objects);
		}
	}

	public static void freeAll(Array<?> objects) {
		synchronized (asyncPool) {
			asyncPool.addAll(objects);
		}
	}

	private static void freeAsync() {
		Object pool = null;
		Class<?> currentType = null;

		for (int i = 0, n = cleaningObjects.size; i < n; i++) {
			Object object = cleaningObjects.get(i);
			if (object == null) {
				continue;
			}

			Class<?> type = object.getClass();
			if (currentType != type) {
				currentType = type;
				pool = type.isArray() ? getArrayPool(type.getComponentType()) : getObjectPool(type);
			}

			if (pool instanceof Pool) {
				@SuppressWarnings("unchecked")
				Pool<Object> objectPool = (Pool<Object>) pool;
				synchronized (objectPool) {
					objectPool.free(object);
				}
			} else {
				@SuppressWarnings("unchecked")
				ArrayPool<Object> arrayPool = (ArrayPool<Object>) pool;
				synchronized (arrayPool) {
					arrayPool.free(object);
				}
			}
		}

		cleaningObjects.clear();
		cleaning = false;
	}

	@Override
	public Void call() throws Exception {
		freeAsync();
		return null;
	}

	@Override
	public void update() {
		if (!cleaning && asyncPool.size > 0) {
			ArrayExt<Object> temp = asyncPool;
			synchronized (asyncPool) {
				cleaning = true;
				asyncPool = cleaningObjects;
			}
			cleaningObjects = temp;
			sort.sort(cleaningObjects, comparatorInstance);
			AsyncService.submit(this);
		}
	}

	private static class FreeObjectsComparator implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			if (o1 == null) {
				return o2 == null ? 0 : -1;
			} else if (o2 == null) {
				return 1;
			} else {
				return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
			}
		}
	}
}
