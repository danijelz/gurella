package com.gurella.engine.base.container;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.application.Application;
import com.gurella.engine.base.container.AsyncCallback.SimpleAsyncCallback;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.resource.DependencyMap;
import com.gurella.engine.utils.ReflectionUtils;

public class ObjectManager {
	private IntMap<ManagedObject> objects = new IntMap<ManagedObject>();
	private IntIntMap counters = new IntIntMap();

	void manage(ManagedObject object) {
		int id = object.id;
		synchronized (counters) {
			counters.put(id, 0);
			objects.put(id, object);
		}
	}

	public <T extends ManagedObject> T obtain(int id) {
		@SuppressWarnings("unchecked")
		SimpleAsyncCallback<T> callback = Pools.obtain(SimpleAsyncCallback.class);
		obtain(id, callback);
		while (!callback.isDone()) {
			try {
				synchronized (this) {
					wait(5);
				}
			} catch (InterruptedException e) {
				continue;
			}
		}

		Throwable exception = callback.getException();
		T resource = callback.getResource();
		Pools.free(callback);

		if (exception != null) {
			throw new GdxRuntimeException("Exception while obtaining resource: " + id, exception);
		}

		return resource;
	}

	public <T extends ManagedObject> void obtain(int id, AsyncCallback<T> callback) {
		int count;
		synchronized (counters) {
			count = counters.getAndIncrement(id, 0, 1);
		}

		if (count == 1) {
			ObtainObjectTask.submit(this, id, callback);
		} else {
			@SuppressWarnings("unchecked")
			T object = (T) objects.get(id);
			callback.onProgress(1);
			callback.onSuccess(object);
		}
	}

	public void obtain(IntArray ids, AsyncCallback<DependencyMap> callback) {
		// TODO AsyncResolver.resolve(this, resourceIds, callback);
	}

	private static class ObtainObjectTask<T extends ManagedObject> implements AsyncTask<Void>, Poolable {
		private ObjectManager container;
		private int id;
		private AsyncCallback<T> callback;

		public static <T extends ManagedObject> void submit(ObjectManager container, int id,
				AsyncCallback<T> callback) {
			@SuppressWarnings("unchecked")
			ObtainObjectTask<T> task = SynchronizedPools.obtain(ObtainObjectTask.class);
			task.container = container;
			task.id = id;
			task.callback = callback;
			Application.ASYNC_EXECUTOR.submit(task);
		}

		@Override
		public Void call() throws Exception {
			JsonValue serializedValue = container.getSerializedValue(id);
			Class<ManagedObject> type = ReflectionUtils.forName(serializedValue.getString("class"));
			Model<ManagedObject> model = Models.getModel(type);
			// TODO garbage
			InitializationContext<ManagedObject> context = new InitializationContext<ManagedObject>();
			context.serializedValue = serializedValue;
			// TODO
			context.template = null;
			context.initializingObject = model.createInstance();
			// TODO find dependencies and notify progress
			model.initInstance(context);

			synchronized (container.counters) {
				container.objects.put(id, context.initializingObject);
			}

			return null;
		}

		@Override
		public void reset() {
			container = null;
			callback = null;
		}
	}
}
