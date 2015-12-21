package com.gurella.engine.base.container;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.application.Application;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.resource.AsyncResourceCallback;
import com.gurella.engine.resource.DependencyMap;

public class Container {
	private IntMap<JsonValue> definitions = new IntMap<JsonValue>();
	private IntMap<ManagedObject> objects = new IntMap<ManagedObject>();
	private IntIntMap counters = new IntIntMap();

	public JsonValue getSerializedValue(int id) {
		return definitions.get(id);
	}

	public <T extends ManagedObject> void obtain(int id, AsyncResourceCallback<T> callback) {
		int count;
		synchronized (counters) {
			count = counters.getAndIncrement(id, 0, 1);
		}

		if (count == 1) {
			ObtainObjectTask.run(this, id, callback);
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

		public static <T extends ManagedObject> void run(Container container, int id,
				AsyncResourceCallback<T> callback) {
			@SuppressWarnings("unchecked")
			ObtainObjectTask<T> instance = SynchronizedPools.obtain(ObtainObjectTask.class);
			instance.container = container;
			instance.id = id;
			instance.callback = callback;
			Application.ASYNC_EXECUTOR.submit(instance);
		}

		@Override
		public Void call() throws Exception {
			JsonValue definition = container.getSerializedValue(id);
			DependencyMap dependencies = DependencyMap.obtain(context, resourceIds)
			resourceReference.obtain(callback);
			
			synchronized (container.counters) {
				container.objects.put(id, value);
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
