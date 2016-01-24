package com.gurella.engine.base.registry;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.application.Application;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.resource.AsyncCallback;
import com.gurella.engine.base.resource.AsyncCallback.SimpleAsyncCallback;
import com.gurella.engine.utils.SynchronizedPools;

//TODO unused
public abstract class ObjectRegistry implements Serializable {
	private static final String TEMPLATES_TAG = "templates";
	private static final String OBJECTS_TAG = "objects";

	private final IntMap<ManagedObject> templates = new IntMap<ManagedObject>();
	private final IntMap<ManagedObject> objects = new IntMap<ManagedObject>();

	protected abstract void clear();

	protected abstract void reset();

	public void addObject(ManagedObject object) {
		objects.put(object.id, object);
	}

	public <T extends ManagedObject> T getObject(int id) {
		@SuppressWarnings("unchecked")
		T object = (T) objects.get(id);
		return object;
	}

	public void addTemplate(ManagedObject template) {
		templates.put(template.id, template);
	}

	public <T extends ManagedObject> T getTemplate(int id) {
		@SuppressWarnings("unchecked")
		T template = (T) templates.get(id);
		return template;
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
			} catch (@SuppressWarnings("unused") InterruptedException ignored) {
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
		@SuppressWarnings("unchecked")
		T object = (T) objects.get(id);
		if (object != null) {
			if (object.isInitialized()) {
				callback.onProgress(1);
				callback.onSuccess(object);
			} else {
				InitObjectTask.submit(this, object, callback);
			}
			return;
		}

		@SuppressWarnings("unchecked")
		T template = (T) templates.get(id);
		if (template == null) {
			throw new GdxRuntimeException("Can't find object by id: " + id);
		}

		object = Objects.duplicate(template);
		InitObjectTask.submit(this, object, callback);
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
		InitializationContext context = SynchronizedPools.obtain(InitializationContext.class);
		context.json = json;
		context.objectRegistry = this;

		JsonValue values = jsonData.get(TEMPLATES_TAG);
		if (values != null) {
			for (JsonValue value : values) {
				ManagedObject template = json.readValue(null, value);
				templates.put(template.id, template);
			}

			for (JsonValue value : values) {
				int id = value.getInt("id");
				ManagedObject template = templates.get(id);
				context.push(template, null, value);
				template.init(context);
				context.pop();
			}
		}

		values = jsonData.get(OBJECTS_TAG);
		if (values != null) {
			for (JsonValue value : values) {
				ManagedObject object = json.readValue(null, value);
				objects.put(object.id, object);
			}

			for (JsonValue value : values) {
				int id = value.getInt("id");
				ManagedObject object = objects.get(id);
				//context.push(object, templates.get(templateMappings.get(id, -1)), value);
				object.init(context);
				context.pop();
			}
		}

		SynchronizedPools.free(context);
	}

	private static class InitObjectTask<T extends ManagedObject> implements AsyncTask<Void>, Poolable {
		private ObjectRegistry registry;
		private ManagedObject object;
		private AsyncCallback<T> callback;

		public static <T extends ManagedObject> void submit(ObjectRegistry registry, ManagedObject object,
				AsyncCallback<T> callback) {
			@SuppressWarnings("unchecked")
			InitObjectTask<T> task = SynchronizedPools.obtain(InitObjectTask.class);
			task.registry = registry;
			task.object = object;
			task.callback = callback;
			Application.ASYNC_EXECUTOR.submit(task);
		}

		@Override
		public Void call() throws Exception {
			Model<? extends ManagedObject> model = Models.getModel(object.getClass());

			// TODO find dependencies and notify progress
			//model.initInstance(context);
			callback.onProgress(1);
			SynchronizedPools.free(this);
			return null;
		}

		@Override
		public void reset() {
			registry = null;
			object = null;
			callback = null;
		}
	}
}
