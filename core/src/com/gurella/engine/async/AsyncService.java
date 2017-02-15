package com.gurella.engine.async;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;

public final class AsyncService {
	public static AsyncServiceConfig config = DefaultAsyncServiceConfig.instance;

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
				executor = config.createAsyncExecutor(1);
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
		return config.getApplication();
	}

	public static AsyncExecutor createAsyncExecutor(final int maxConcurrent) {
		return config.createAsyncExecutor(maxConcurrent);
	}

	public static <T> AsyncResult<T> submit(final AsyncTask<T> task) {
		return getInstance().submit(task);
	}

	private static class Cleaner implements ApplicationShutdownListener {
		@Override
		public void shutdown() {
			EventService.unsubscribe(this);
			AsyncExecutor removed;

			synchronized (instances) {
				removed = instances.remove(getApplication());

				if (removed == lastSelected) {
					lastSelected = null;
					lastApp = null;
				}
			}

			removed.dispose();
		}
	}

	public interface AsyncServiceConfig {
		Application getApplication();

		AsyncExecutor createAsyncExecutor(final int maxConcurrent);
	}

	private static class DefaultAsyncServiceConfig implements AsyncServiceConfig {
		static final AsyncServiceConfig instance = new DefaultAsyncServiceConfig();

		private DefaultAsyncServiceConfig() {
		}

		@Override
		public Application getApplication() {
			return Gdx.app;
		}

		@Override
		public AsyncExecutor createAsyncExecutor(final int maxConcurrent) {
			return new AsyncExecutor(maxConcurrent);
		}
	}
}
