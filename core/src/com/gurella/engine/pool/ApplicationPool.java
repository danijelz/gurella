package com.gurella.engine.pool;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.async.AsyncService;
import com.gurella.engine.factory.Factories;
import com.gurella.engine.factory.Factory;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.utils.Values;
import com.gurella.engine.utils.priority.Priorities;
import com.gurella.engine.utils.priority.Priority;

//TODO factory pools, 
//TODO handle Disposables
@Priorities({ @Priority(value = CommonUpdatePriority.cleanupPriority, type = ApplicationUpdateListener.class),
		@Priority(value = CommonUpdatePriority.cleanupPriority, type = ApplicationDebugUpdateListener.class) })
public class ApplicationPool implements AsyncTask<Void>, ApplicationUpdateListener, ApplicationDebugUpdateListener {
	private final FreeObjectsComparator comparatorInstance = new FreeObjectsComparator();

	private final BooleanArrayPool booleanArrayPool;
	private final ByteArrayPool byteArrayPool;
	private final CharArrayPool charArrayPool;
	private final ShortArrayPool shortArrayPool;
	private final IntArrayPool intArrayPool;
	private final LongArrayPool longArrayPool;
	private final FloatArrayPool floatArrayPool;
	private final DoubleArrayPool doubleArrayPool;
	private final SimpleObjectArrayPool objectArrayPool;

	private final ObjectMap<Class<?>, Pool<?>> pools = new ObjectMap<Class<?>, Pool<?>>();
	private final ObjectMap<Class<?>, ArrayPool<?>> arrayPools = new ObjectMap<Class<?>, ArrayPool<?>>();

	private boolean cleaning;
	private Array<Object> asyncPool = new Array<Object>(128);
	private Array<Object> cleaningObjects = new Array<Object>(128);
	private final Sort sort = new Sort();

	ApplicationPool() {
		arrayPools.put(boolean.class, booleanArrayPool = new BooleanArrayPool());
		arrayPools.put(byte.class, byteArrayPool = new ByteArrayPool());
		arrayPools.put(char.class, charArrayPool = new CharArrayPool());
		arrayPools.put(short.class, shortArrayPool = new ShortArrayPool());
		arrayPools.put(int.class, intArrayPool = new IntArrayPool());
		arrayPools.put(long.class, longArrayPool = new LongArrayPool());
		arrayPools.put(float.class, floatArrayPool = new FloatArrayPool());
		arrayPools.put(double.class, doubleArrayPool = new DoubleArrayPool());
		arrayPools.put(Object.class, objectArrayPool = new SimpleObjectArrayPool());
	}

	private <T> Pool<T> getObjectPool(Class<T> type) {
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

	private <T> ArrayPool<T> getArrayPool(Class<?> componentType) {
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

	public <T> T obtain(Class<T> type) {
		Pool<T> pool = getObjectPool(type);
		synchronized (pool) {
			return pool.obtain();
		}
	}

	public boolean[] obtainBooleanArray(int length, float maxDeviation) {
		synchronized (booleanArrayPool) {
			return booleanArrayPool.obtain(length, length + (int) (length * maxDeviation));
		}
	}

	public boolean[] obtainBooleanArray(int length, int maxLength) {
		synchronized (booleanArrayPool) {
			return booleanArrayPool.obtain(length, maxLength);
		}
	}

	public byte[] obtainByteArray(int length, float maxDeviation) {
		synchronized (byteArrayPool) {
			return byteArrayPool.obtain(length, length + (int) (length * maxDeviation));
		}
	}

	public byte[] obtainByteArray(int length, int maxLength) {
		synchronized (byteArrayPool) {
			return byteArrayPool.obtain(length, maxLength);
		}
	}

	public char[] obtainCharArray(int length, float maxDeviation) {
		synchronized (charArrayPool) {
			return charArrayPool.obtain(length, length + (int) (length * maxDeviation));
		}
	}

	public char[] obtainCharArray(int length, int maxLength) {
		synchronized (charArrayPool) {
			return charArrayPool.obtain(length, maxLength);
		}
	}

	public short[] obtainShortArray(int length, float maxDeviation) {
		synchronized (shortArrayPool) {
			return shortArrayPool.obtain(length, length + (int) (length * maxDeviation));
		}
	}

	public short[] obtainShortArray(int length, int maxLength) {
		synchronized (shortArrayPool) {
			return shortArrayPool.obtain(length, maxLength);
		}
	}

	public int[] obtainIntArray(int length, float maxDeviation) {
		synchronized (intArrayPool) {
			return intArrayPool.obtain(length, length + (int) (length * maxDeviation));
		}
	}

	public int[] obtainIntArray(int length, int maxLength) {
		synchronized (intArrayPool) {
			return intArrayPool.obtain(length, maxLength);
		}
	}

	public long[] obtainLongArray(int length, float maxDeviation) {
		synchronized (longArrayPool) {
			return longArrayPool.obtain(length, length + (int) (length * maxDeviation));
		}
	}

	public long[] obtainLongArray(int length, int maxLength) {
		synchronized (longArrayPool) {
			return longArrayPool.obtain(length, maxLength);
		}
	}

	public float[] obtainFloatArray(int length, float maxDeviation) {
		synchronized (floatArrayPool) {
			return floatArrayPool.obtain(length, length + (int) (length * maxDeviation));
		}
	}

	public float[] obtainFloatArray(int length, int maxLength) {
		synchronized (floatArrayPool) {
			return floatArrayPool.obtain(length, maxLength);
		}
	}

	public double[] obtainDoubleArray(int length, float maxDeviation) {
		synchronized (doubleArrayPool) {
			return doubleArrayPool.obtain(length, length + (int) (length * maxDeviation));
		}
	}

	public double[] obtainDoubleArray(int length, int maxLength) {
		synchronized (doubleArrayPool) {
			return doubleArrayPool.obtain(length, maxLength);
		}
	}

	public Object[] obtainObjectArray(int length, float maxDeviation) {
		synchronized (objectArrayPool) {
			return objectArrayPool.obtain(length, length + (int) (length * maxDeviation));
		}
	}

	public Object[] obtainObjectArray(int length, int maxLength) {
		synchronized (objectArrayPool) {
			return objectArrayPool.obtain(length, maxLength);
		}
	}

	public <T> T[] obtainArray(Class<T> componentType, int length, float maxDeviation) {
		return obtainArrayInternal(componentType, length, length + (int) (length * maxDeviation));
	}

	public <T> T[] obtainArray(Class<T> componentType, int length, int maxLength) {
		return obtainArrayInternal(componentType, length, maxLength);
	}

	private <T> T obtainArrayInternal(Class<?> componentType, int length, int maxLength) {
		@SuppressWarnings("unchecked")
		ArrayPool<T> pool = (ArrayPool<T>) getArrayPool(componentType);
		synchronized (pool) {
			return pool.obtain(length, maxLength);
		}
	}

	public <T> void free(T object) {
		synchronized (asyncPool) {
			asyncPool.add(object);
		}
	}

	public void freeAll(Object... objects) {
		synchronized (asyncPool) {
			asyncPool.addAll(objects);
		}
	}

	public void freeAll(Array<?> objects) {
		synchronized (asyncPool) {
			asyncPool.addAll(objects);
		}
	}

	private void freeAsync() {
		Object pool = null;
		Class<?> currentType = null;

		try {
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
		} catch (Exception e) {
			Gdx.app.log(PoolService.class.getSimpleName(), "Error occured while freeing objects", e);
		}
	}

	@Override
	public Void call() throws Exception {
		try {
			freeAsync();
		} finally {
			cleaning = false;
		}
		return null;
	}

	@Override
	public void update() {
		if (!cleaning && asyncPool.size > 0) {
			cleaning = true;
			prepareForCleaning();
			AsyncService.submit(this);
		}
	}

	private void prepareForCleaning() {
		Array<Object> temp = asyncPool;
		synchronized (asyncPool) {
			asyncPool = cleaningObjects;
		}
		cleaningObjects = temp;
		sort.sort(cleaningObjects, comparatorInstance);
	}

	@Override
	public void debugUpdate() {
		cleanAll();
	}

	void cleanAll() {
		prepareForCleaning();
		freeAsync();
		synchronized (asyncPool) {
			if (asyncPool.size == 0) {
				return;
			}
		}
		cleanAll();
	}

	private static class FreeObjectsComparator implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			if (o1 == null) {
				return o2 == null ? 0 : -1;
			} else if (o2 == null) {
				return 1;
			} else {
				return o1.getClass().getName().compareTo(o2.getClass().getName());
			}
		}
	}
}
