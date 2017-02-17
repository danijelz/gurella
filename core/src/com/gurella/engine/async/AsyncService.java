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
	private static AsyncServiceConfig config = DefaultAsyncServiceConfig.instance;
	private static final ObjectMap<Application, AsyncExecutor> instances = new ObjectMap<Application, AsyncExecutor>();

	private static AsyncExecutor singleton;
	private static AsyncExecutor lastSelected;
	private static Application lastApp;

	private AsyncService() {
	}

	private static AsyncExecutor getInstance() {
		if (singleton != null) {
			return singleton;
		}

		AsyncExecutor executor;
		boolean subscribe = false;

		synchronized (instances) {
			if (!config.isMultiApplicationEnvironment()) {
				singleton = config.createAsyncExecutor(1);
				return singleton;
			}

			Application app = getCurrentApplication();
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

	public static Application getCurrentApplication() {
		return config.getApplication();
	}

	public static boolean isMultiApplicationEnvironment() {
		return config.isMultiApplicationEnvironment();
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
				removed = instances.remove(getCurrentApplication());

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

		boolean isMultiApplicationEnvironment();
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

		@Override
		public boolean isMultiApplicationEnvironment() {
			return false;
		}
	}
}
