package com.gurella.engine.async;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;

public final class AsyncService {
	static final ThreadLocal<Application> applicationContext = new ThreadLocal<Application>();

	private static final ObjectMap<Application, AsyncExecutor> instances = new ObjectMap<Application, AsyncExecutor>();
	private static AsyncExecutor lastSelected;
	private static Application lastApp;

	private AsyncService() {
	}

	private static AsyncExecutor getInstance() {
		AsyncExecutor executor;
		boolean subscribe = false;

		synchronized (instances) {
			Application app = getApplication();
			if (lastApp == app) {
				return lastSelected;
			}

			executor = instances.get(app);
			if (executor == null) {
				executor = new AsyncExecutor(1);
				instances.put(app, executor);
				subscribe = true;
			}

			lastApp = app;
			lastSelected = executor;
		}

		if (subscribe) {
			EventService.subscribe(new Cleaner());
		}

		return executor;
	}

	public static Application getApplication() {
		Application application = applicationContext.get();
		return application == null ? Gdx.app : application;
	}

	public static <T> AsyncResult<T> submit(final AsyncTask<T> task) {
		return getInstance().submit(task);
	}

	public static <T> void submit(final AsyncTask<T> task, final AsyncCallback<T> callback) {
		@SuppressWarnings("unchecked")
		CallbackTask<T> callbackTask = PoolService.obtain(CallbackTask.class);
		callbackTask.task = task;
		callbackTask.callback = callback;
		getInstance().submit(callbackTask);
	}

	private static class CallbackTask<T> implements AsyncTask<Void>, Poolable {
		private AsyncTask<T> task;
		private AsyncCallback<T> callback;

		@Override
		public Void call() throws Exception {
			T value;
			try {
				value = task.call();
			} catch (Exception e) {
				callback.onException(e);
				return null;
			}
			callback.onSuccess(value);
			PoolService.free(this);
			return null;
		}

		@Override
		public void reset() {
			task = null;
			callback = null;
		}
	}

	private static class Cleaner implements ApplicationShutdownListener {
		@Override
		public void shutdown() {
			EventService.unsubscribe(this);
			AsyncExecutor removed;

			synchronized (instances) {
				removed = instances.remove(Gdx.app);

				if (removed == lastSelected) {
					lastSelected = null;
					lastApp = null;
				}
			}

			removed.dispose();
		}
	}
}
