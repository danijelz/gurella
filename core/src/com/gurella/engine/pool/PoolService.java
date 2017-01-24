package com.gurella.engine.pool;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.engine.utils.priority.Priority;

public final class PoolService {
	private static final IdentityMap<Application, ApplicationPool> instances = new IdentityMap<Application, ApplicationPool>();

	private static ApplicationPool lastSelected;
	private static Application lastApp;

	private PoolService() {
	}

	private static ApplicationPool getPool() {
		ApplicationPool pool;
		boolean subscribe = false;

		synchronized (instances) {
			Application app = Gdx.app;
			if (lastApp == app) {
				return lastSelected;
			}

			pool = instances.get(app);
			if (pool == null) {
				pool = new ApplicationPool();
				instances.put(app, pool);
				subscribe = true;
			}

			lastApp = app;
			lastSelected = pool;
		}

		if (subscribe) {
			EventService.subscribe(pool);
			EventService.subscribe(new Cleaner());
		}

		return pool;
	}

	public static <T> T obtain(Class<T> type) {
		return getPool().obtain(type);
	}

	public static boolean[] obtainBooleanArray(int length, float maxDeviation) {
		return getPool().obtainBooleanArray(length, maxDeviation);
	}

	public static boolean[] obtainBooleanArray(int length, int maxLength) {
		return getPool().obtainBooleanArray(length, maxLength);
	}

	public static byte[] obtainByteArray(int length, float maxDeviation) {
		return getPool().obtainByteArray(length, maxDeviation);
	}

	public static byte[] obtainByteArray(int length, int maxLength) {
		return getPool().obtainByteArray(length, maxLength);
	}

	public static char[] obtainCharArray(int length, float maxDeviation) {
		return getPool().obtainCharArray(length, maxDeviation);
	}

	public static char[] obtainCharArray(int length, int maxLength) {
		return getPool().obtainCharArray(length, maxLength);
	}

	public static short[] obtainShortArray(int length, float maxDeviation) {
		return getPool().obtainShortArray(length, maxDeviation);
	}

	public static short[] obtainShortArray(int length, int maxLength) {
		return getPool().obtainShortArray(length, maxLength);
	}

	public static int[] obtainIntArray(int length, float maxDeviation) {
		return getPool().obtainIntArray(length, maxDeviation);
	}

	public static int[] obtainIntArray(int length, int maxLength) {
		return getPool().obtainIntArray(length, maxLength);
	}

	public static long[] obtainLongArray(int length, float maxDeviation) {
		return getPool().obtainLongArray(length, maxDeviation);
	}

	public static long[] obtainLongArray(int length, int maxLength) {
		return getPool().obtainLongArray(length, maxLength);
	}

	public static float[] obtainFloatArray(int length, float maxDeviation) {
		return getPool().obtainFloatArray(length, maxDeviation);
	}

	public static float[] obtainFloatArray(int length, int maxLength) {
		return getPool().obtainFloatArray(length, maxLength);
	}

	public static double[] obtainDoubleArray(int length, float maxDeviation) {
		return getPool().obtainDoubleArray(length, maxDeviation);
	}

	public static double[] obtainDoubleArray(int length, int maxLength) {
		return getPool().obtainDoubleArray(length, maxLength);
	}

	public static Object[] obtainObjectArray(int length, float maxDeviation) {
		return getPool().obtainObjectArray(length, maxDeviation);
	}

	public static Object[] obtainObjectArray(int length, int maxLength) {
		return getPool().obtainObjectArray(length, maxLength);
	}

	public static <T> T[] obtainArray(Class<T> componentType, int length, float maxDeviation) {
		return getPool().obtainArray(componentType, length, maxDeviation);
	}

	public static <T> T[] obtainArray(Class<T> componentType, int length, int maxLength) {
		return getPool().obtainArray(componentType, length, maxLength);
	}

	public static <T> void free(T object) {
		getPool().free(object);
	}

	public static void freeAll(Object... objects) {
		getPool().freeAll(objects);
	}

	public static void freeAll(Array<?> objects) {
		getPool().freeAll(objects);
	}

	@Priority(value = 1000, type = ApplicationShutdownListener.class)
	private static class Cleaner implements ApplicationShutdownListener {
		@Override
		public void shutdown() {
			EventService.unsubscribe(this);
			ApplicationPool pool;

			synchronized (instances) {
				pool = instances.remove(Gdx.app);

				if (pool == lastSelected) {
					lastSelected = null;
					lastApp = null;
				}
			}

			EventService.unsubscribe(pool);
		}
	}
}
