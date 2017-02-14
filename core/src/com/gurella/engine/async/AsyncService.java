package com.gurella.engine.async;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;

public final class AsyncService {
	private static final ThreadLocal<Application> contextApplication = new ThreadLocal<Application>();
	private static final ObjectMap<Application, ContextAsyncExecutor> instances = new ObjectMap<Application, ContextAsyncExecutor>();
	private static ContextAsyncExecutor lastSelected;
	private static Application lastApp;

	private AsyncService() {
	}

	private static ContextAsyncExecutor getInstance() {
		ContextAsyncExecutor executor;
		boolean subscribe = false;

		synchronized (instances) {
			Application app = getApplication();
			if (lastApp == app) {
				return lastSelected;
			}

			executor = instances.get(app);
			if (executor == null) {
				executor = new ContextAsyncExecutor(1);
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
		Application application = contextApplication.get();
		return application == null ? Gdx.app : application;
	}

	public static AsyncExecutor createAsyncExecutor(final int maxConcurrent) {
		return new ContextAsyncExecutor(maxConcurrent);
	}

	public static <T> AsyncResult<T> submit(final AsyncTask<T> task) {
		return getInstance().submit(task);
	}

	public static <T> void submit(final AsyncTask<T> task, final AsyncCallback<T> callback) {
		getInstance().submit(task, callback);
	}

	private static class ContextAsyncExecutor extends AsyncExecutor {
		private final Application contextApp;

		public ContextAsyncExecutor(int maxConcurrent) {
			super(maxConcurrent);
			this.contextApp = getApplication();
		}

		public <T> void submit(final AsyncTask<T> task, final AsyncCallback<T> callback) {
			super.submit(CallbackTask.obtain(task, callback));
		}

		@Override
		public <T> AsyncResult<T> submit(AsyncTask<T> task) {
			return super.submit(ContextTask.obtain(contextApp, task));
		}
	}

	private static class ContextTask<T> implements AsyncTask<T>, Poolable {
		private Application contextApp;
		private AsyncTask<T> task;

		static <T> ContextTask<T> obtain(Application contextApp, AsyncTask<T> task) {
			@SuppressWarnings("unchecked")
			ContextTask<T> contextTask = PoolService.obtain(ContextTask.class);
			contextTask.contextApp = contextApp;
			contextTask.task = task;
			return contextTask;
		}

		@Override
		public T call() throws Exception {
			try {
				contextApplication.set(contextApp);
				return task.call();
			} finally {
				PoolService.free(this);
				contextApplication.set(null);
			}
		}

		@Override
		public void reset() {
			contextApp = null;
			task = null;
		}
	}

	private static class CallbackTask<T> implements AsyncTask<Void>, Poolable {
		private Application contextApp;
		private AsyncTask<T> task;
		private AsyncCallback<T> callback;

		static <T> CallbackTask<T> obtain(AsyncTask<T> task, AsyncCallback<T> callback) {
			@SuppressWarnings("unchecked")
			CallbackTask<T> callbackTask = PoolService.obtain(CallbackTask.class);
			callbackTask.contextApp = getApplication();
			callbackTask.task = task;
			callbackTask.callback = callback;
			return callbackTask;
		}

		@Override
		public Void call() throws Exception {
			try {
				contextApplication.set(contextApp);
				T value;
				try {
					value = task.call();
				} catch (Exception e) {
					callback.onException(e);
					return null;
				}
				callback.onSuccess(value);
			} finally {
				PoolService.free(this);
				contextApplication.set(null);
			}
			return null;
		}

		@Override
		public void reset() {
			contextApp = null;
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
