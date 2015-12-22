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
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.ModelUtils;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.resource.AsyncResourceCallback;
import com.gurella.engine.resource.AsyncResourceCallback.SimpleAsyncResourceCallback;
import com.gurella.engine.resource.DependencyMap;
import com.gurella.engine.utils.ReflectionUtils;

public class Container {
	private IntMap<JsonValue> definitions = new IntMap<JsonValue>();
	private IntMap<ManagedObject> objects = new IntMap<ManagedObject>();
	private IntIntMap counters = new IntIntMap();

	public JsonValue getSerializedValue(int id) {
		return definitions.get(id);
	}

	public <T extends ManagedObject> T obtain(int id) {
		@SuppressWarnings("unchecked")
		SimpleAsyncResourceCallback<T> callback = Pools.obtain(SimpleAsyncResourceCallback.class);
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

	public <T extends ManagedObject> void obtain(int id, AsyncResourceCallback<T> callback) {
		int count;
		synchronized (counters) {
			count = counters.getAndIncrement(id, 0, 1);
		}

		if (count == 1) {
			ObtainObjectTask.submit(this, id, callback);
		} else {
			@SuppressWarnings("unchecked")
			T object = (T) objects.get(id);
			callback.handleProgress(1);
			callback.handleResource(object);
		}
	}

	public void obtain(IntArray ids, AsyncResourceCallback<DependencyMap> callback) {
		AsyncResolver.resolve(this, resourceIds, callback);
	}

	private static class ObtainObjectTask<T extends ManagedObject> implements AsyncTask<Void>, Poolable {
		private Container container;
		private int id;
		private AsyncResourceCallback<T> callback;

		public static <T extends ManagedObject> void submit(Container container, int id,
				AsyncResourceCallback<T> callback) {
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
			Model<ManagedObject> model = ModelUtils.getModel(type);
			//TODO garbage
			InitializationContext<ManagedObject> context = new InitializationContext<ManagedObject>();
			context.serializedValue = serializedValue;
			//TODO
			context.template = null;
			context.initializingObject = model.createInstance();
			//TODO find dependencies
			context.initializingObject

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
