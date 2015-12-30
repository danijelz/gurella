package com.gurella.engine.base.registry;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.application.Application;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.registry.AsyncCallback.SimpleAsyncCallback;
import com.gurella.engine.base.registry.ObjectManager.ObtainObjectTask;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.utils.ReflectionUtils;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedSet;

//TODO unused
public abstract class ObjectRegistry implements Serializable {
	private static final String TEMPLATES_TAG = "templates";
	private static final String OBJECTS_TAG = "templates";

	private final IntMap<ManagedObject> templates = new IntMap<ManagedObject>();
	private final IntMap<ManagedObject> objects = new IntMap<ManagedObject>();
	private IntIntMap templateMappings = new IntIntMap();

	protected abstract void clear();

	protected abstract void reset();

	void addObject(ManagedObject object) {
		objects.put(object.id, object);
	}

	void addTemplate(ManagedObject object) {
		templates.put(object.id, object);
	}

	private <T extends ManagedObject> T find(int id) {
		ManagedObject managedObject = objects.get(id);
		if (managedObject == null) {
			managedObject = templates.get(id);
		}

		if (managedObject == null) {
			throw new GdxRuntimeException("Can't find object by id: " + id);
		}

		@SuppressWarnings("unchecked")
		T casted = (T) managedObject;
		return casted;
	}

	public <T extends ManagedObject> T obtain(int id) {
		@SuppressWarnings("unchecked")
		SimpleAsyncCallback<T> callback = SynchronizedPools.obtain(SimpleAsyncCallback.class);
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
		T object = callback.getValue();
		SynchronizedPools.free(callback);

		if (exception != null) {
			throw new GdxRuntimeException("Exception while obtaining resource: " + id, exception);
		}

		return object;
	}

	public <T extends ManagedObject> void obtain(int id, AsyncCallback<T> callback) {
		ObtainObjectTask.submit(this, id, callback);

		if (count == 1) {
			ObtainObjectTask.submit(this, id, callback);
		} else {
			@SuppressWarnings("unchecked")
			T object = (T) objects.get(id);
			callback.onProgress(1);
			callback.onSuccess(object);
		}
	}

	@Override
	public void write(Json json) {
		if (templates.size > 0) {
			json.writeArrayStart(TEMPLATES_TAG);
			Array<ManagedObject> templatesItems = templates.values().toArray();
			templatesItems.sort();
			for (int i = 0; i < templatesItems.size; i++) {
				ManagedObject template = templatesItems.get(i);
				json.writeValue(template);
			}
			json.writeArrayEnd();
		}

		if (objects.size > 0) {
			json.writeArrayStart(OBJECTS_TAG);
			Array<ManagedObject> objectsItems = objects.values().toArray();
			objectsItems.sort();
			for (int i = 0; i < objectsItems.size; i++) {
				ManagedObject object = objectsItems.get(i);
				json.writeValue(object);
			}
			json.writeArrayEnd();
		}
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		JsonValue values = jsonData.get(TEMPLATES_TAG);
		if (values != null) {
			for (JsonValue value : values) {
				ManagedObject template = json.readValue(null, value);
				addTemplate(template);
				addObject(template);
			}

			int i = 0;
			Array<ManagedObject> templateItems = templates.orderedItems();
			for (JsonValue value : values) {
				ManagedObject template = templateItems.get(i++);
				template.readProperties(json, value);
			}
		}

		values = jsonData.get(OBJECTS_TAG);
		if (values != null) {
			for (JsonValue value : values) {
				ManagedObject object = json.readValue(null, value);
				addObject(object);
				addObject(object);
			}

			int i = 0;
			Array<ManagedObject> objectItems = objects.orderedItems();
			for (JsonValue value : values) {
				ManagedObject object = objectItems.get(i++);
				object.readProperties(json, value);
			}
		}
	}

	private static class ObtainObjectTask<T extends ManagedObject> implements AsyncTask<Void>, Poolable {
		private ObjectRegistry registry;
		private int id;
		private AsyncCallback<T> callback;

		public static <T extends ManagedObject> void submit(ObjectRegistry registry, int id,
				AsyncCallback<T> callback) {
			@SuppressWarnings("unchecked")
			ObtainObjectTask<T> task = SynchronizedPools.obtain(ObtainObjectTask.class);
			task.registry = registry;
			task.id = id;
			task.callback = callback;
			Application.ASYNC_EXECUTOR.submit(task);
		}

		@Override
		public Void call() throws Exception {
			@SuppressWarnings("unchecked")
			T object = (T) registry.objects.get(id);
			if(object != null) {
				if(!object.isInitialized()) {
					object.initInternal(asyncCallback);
				}
			}
			
			@SuppressWarnings("unchecked")
			T template = (T) registry.templates.get(id);
			if (template == null) {
				throw new GdxRuntimeException("Can't find object by id: " + id);
			}
			
			Model<ManagedObject> model = Models.getModel(type);
			// TODO garbage
			InitializationContext<ManagedObject> context = new InitializationContext<ManagedObject>();
			// TODO
			context.template = null;
			context.initializingObject = model.createInstance();
			// TODO find dependencies and notify progress
			model.initInstance(context);

			return null;
		}

		@Override
		public void reset() {
			registry = null;
			callback = null;
		}
	}
}
